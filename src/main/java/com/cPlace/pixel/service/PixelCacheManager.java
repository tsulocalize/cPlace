package com.cPlace.pixel.service;

import com.cPlace.pixel.domain.Pixel;
import lombok.RequiredArgsConstructor;
import org.redisson.api.RBitSet;
import org.redisson.api.RBucket;
import org.redisson.api.RScript;
import org.redisson.api.RStream;
import org.redisson.api.RedissonClient;
import org.redisson.api.StreamMessageId;
import org.redisson.api.stream.StreamAddArgs;
import org.redisson.api.stream.StreamReadArgs;
import org.redisson.client.codec.StringCodec;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.BinaryMessage;
import java.nio.ByteBuffer;
import java.time.Clock;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class PixelCacheManager {

    private static final int UNIT_BYTE_SIZE = 6;
    private static final int TIME_LIMIT = 500; // ms
    private static final String LUA_SCRIPT = """
            local stream_key = KEYS[1]
            local cache_key = KEYS[2]
            local count = tonumber(KEYS[3]) -- 가져올 메시지 개수
            
            -- lastUpdated 값 가져오기 (기본값은 '0-0')
            local last_updated = redis.call('GET', 'last_updated')
            if not last_updated then
                last_updated = '0-0'
            end
            
            local messages = redis.call('XREAD', 'COUNT', count, 'STREAMS', stream_key, last_updated)
            
            if not messages or #messages == 0 then
                return 'NO MESSAGES'
            end
            
            local latest_message_id = last_updated
            
            for _, stream in ipairs(messages) do
                for _, message in ipairs(stream[2]) do
                    local message_id = message[1]  -- 메시지 ID
                    local data = message[2]        -- 메시지의 필드 데이터
                    local xy_value = data[1]  -- 첫 번째 데이터에 x-y 값이 들어 있음
                    local x, y = xy_value:match('^(%d+)%-(%d+)$')  -- '003-005'에서 x, y 추출
                    local offset = 8 * (tonumber(x) + 500 * tonumber(y))
            
                    if x and y then
                        redis.call('BITFIELD', cache_key, 'SET', 'u8', offset, tonumber(data[2]))
                    end
                    latest_message_id = message_id
                end
            end
            
            -- 마지막으로 읽은 메시지 ID 업데이트
            redis.call('SET', 'last_updated', latest_message_id)
            
            -- 최대 100,000개 메시지만 유지 (근사치 유지)
            redis.call('XTRIM', stream_key, 'MAXLEN', '~', 100000)
            
            return 'OK'
            """;
    private static final String DIRTY_SET = "dirtySet";
    private static final String CANVAS_BIT = "canvas_bit";

    private final Clock clock;
    private final RedissonClient redissonClient;
    private final PixelWebSocketManager webSocketManager;
    private long lastBroadcastAt = 0L;

    @Value("${main}")
    private boolean isMain;

    public byte[] getCurrentPixels() {
        RBucket<String> lastUpdated = redissonClient.getBucket("last_updated");

        long millisecond = Long.parseLong(lastUpdated.get().split("-")[0]);
        RBitSet canvasBit = redissonClient.getBitSet(CANVAS_BIT);
        byte[] canvasBitByteArray = canvasBit.toByteArray();

        ByteBuffer buffer = ByteBuffer.allocate(8 + canvasBitByteArray.length);
        buffer.putLong(millisecond);
        buffer.put(canvasBitByteArray);

        return buffer.array();
    }

    public byte[] getDirtySet(long lastUpdated) {
        List<byte[]> dirtySetAfter = getDirtySetAfter(lastUpdated);
        ByteBuffer buffer = ByteBuffer.allocate(UNIT_BYTE_SIZE * dirtySetAfter.size());
        for (byte[] bytes : dirtySetAfter) {
            buffer.put(bytes);
        }
        return buffer.array();
    }

    public void addDirtySet(Pixel pixel) {
        RStream<String, String> dirtySet = redissonClient.getStream(DIRTY_SET);
        dirtySet.add(StreamAddArgs.entry("%03d-%03d".formatted(pixel.x(), pixel.y()), String.valueOf(pixel.color().ordinal())));
    }

    public void addDirtySet(List<Pixel> pixels) {
        RStream<String, String> dirtySet = redissonClient.getStream(DIRTY_SET);
        Map<String, String> addArgsMap = new HashMap<>();
        pixels.forEach(pixel -> addArgsMap.put("%03d-%03d".formatted(pixel.x(), pixel.y()), String.valueOf(pixel.color().ordinal())));
        dirtySet.add(StreamAddArgs.entries(addArgsMap));
    }

    @Scheduled(initialDelayString = "#{${redis.dirtySet-interval}}", fixedRateString = "#{${redis.dirtySet-interval}}")
    void applyDirtySetToCache() {
        if (!isMain) return;

        RScript script = redissonClient.getScript(StringCodec.INSTANCE);
        script.eval(RScript.Mode.READ_WRITE, LUA_SCRIPT, RScript.ReturnType.VALUE,
                List.of(DIRTY_SET, CANVAS_BIT, "100"));
    }

    @Scheduled(fixedDelay = TIME_LIMIT)
    synchronized void broadcastDirtySet() {
        long epochMilliSecond = LocalDateTime.now(clock).toEpochSecond(ZoneOffset.ofHours(9)) * 1000;

        BinaryMessage message = convertToBinaryMessage(getDirtySetAfter(lastBroadcastAt));
        if (message.getPayloadLength() == 0) return;
        webSocketManager.sendPixelChangeToAll(message);
        lastBroadcastAt = epochMilliSecond;
    }

    private List<byte[]> getDirtySetAfter(long epochMilliSecond) {
        RStream<String, String> dirtySet = redissonClient.getStream(DIRTY_SET);

        return dirtySet.read(StreamReadArgs.greaterThan(new StreamMessageId(epochMilliSecond)))
                        .values().stream()
                        .flatMap(dirtySetMap -> dirtySetMap.entrySet().stream()
                                .map(entry -> convertToBytes(entry.getKey(), entry.getValue())))
                        .toList();
    }

    private BinaryMessage convertToBinaryMessage(List<byte[]> bytes) {
        ByteBuffer responseBuffer = ByteBuffer.allocate(UNIT_BYTE_SIZE * bytes.size());

        for (byte[] b : bytes) {
            responseBuffer.put(b);
        }
        responseBuffer.flip();

        return new BinaryMessage(responseBuffer);
    }

    private byte[] convertToBytes(String coordinate, String color) {
        String[] split = coordinate.split("-"); // "%03d-%03d"

        ByteBuffer buffer = ByteBuffer.allocate(6);
        buffer.putShort(Short.parseShort(split[0]));
        buffer.putShort(Short.parseShort(split[1]));
        buffer.put(Byte.parseByte(color));
        buffer.put((byte) 0); // padding

        return buffer.array();
    }
}
