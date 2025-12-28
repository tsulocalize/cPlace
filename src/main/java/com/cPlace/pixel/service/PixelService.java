package com.cPlace.pixel.service;

import com.cPlace.chzzk.domain.ChzzkMember;
import com.cPlace.chzzk.respository.ChzzkMemberRepository;
import com.cPlace.pixel.config.PixelProperties;
import com.cPlace.pixel.domain.Color;
import com.cPlace.pixel.domain.Pixel;
import com.cPlace.pixel.domain.PixelMapHistory;
import com.cPlace.pixel.domain.PixelUpdateHistory;
import com.cPlace.pixel.dto.PixelDrawHistoryResponse;
import com.cPlace.pixel.exception.PixelException;
import com.cPlace.pixel.exception.PixelExceptionCode;
import com.cPlace.pixel.repository.PixelMapHistoryRepository;
import com.cPlace.pixel.repository.PixelUpdateHistoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Limit;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.time.Clock;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class PixelService {

    @Value("${main}")
    private boolean isMain;

    private final Clock clock;
    private final PixelUpdateHistoryRepository pixelUpdateHistoryRepository;
    private final PixelMapHistoryRepository pixelMapHistoryRepository;
    private final PixelProperties pixelProperties;
    private final ChzzkMemberRepository chzzkMemberRepository;
    private final PixelCacheManager pixelCacheManager;

    public byte[] readCurrentPixels() {
        return pixelCacheManager.getCurrentPixels();
    }

    public byte[] readDirtySet(long lastUpdated) {
        return pixelCacheManager.getDirtySet(lastUpdated);
    }

    @Transactional
    public void changePixel(int x, int y, Color color, ChzzkMember chzzkMember) {
        LocalDateTime now = LocalDateTime.now(clock);
        validateParameters(x, y, chzzkMember, now);

        Pixel pixel = new Pixel(x, y, color, now.toEpochSecond(ZoneOffset.UTC));
        pixelCacheManager.addDirtySet(pixel);
        pixelUpdateHistoryRepository.save(new PixelUpdateHistory(pixel, chzzkMember));
        chzzkMember.updateLastActiveAt(now);
        chzzkMemberRepository.save(chzzkMember);
    }

    private void validateParameters(int x, int y, ChzzkMember chzzkMember, LocalDateTime now) {
        validateTimeLimit(now, chzzkMember.getLastActiveAt());
        validateCoordinates(x, y);
        if (chzzkMember.isBanned()) {
            throw new PixelException(PixelExceptionCode.AUTHORIZATION_INVALID);
        }
    }

    private void validateTimeLimit(LocalDateTime now, LocalDateTime lastActiveAt) {
        if (lastActiveAt == null) return;
        LocalDateTime availableFrom = lastActiveAt.plusSeconds(pixelProperties.getDrawingTimeLimit() / 1_000);
        if (availableFrom.isAfter(now)) {
            throw new PixelException(PixelExceptionCode.TIME_LIMITED);
        }
    }

    private void validateCoordinates(int x, int y) {
        if (x < 0 || x >= pixelProperties.getPixelSize()) {
            throw new PixelException(PixelExceptionCode.PIXEL_NOT_FOUND);
        }
        if (y < 0 || y >= pixelProperties.getPixelSize()) {
            throw new PixelException(PixelExceptionCode.PIXEL_NOT_FOUND);
        }
    }

    @Transactional(readOnly = true)
    public List<PixelDrawHistoryResponse> readUpdateHistory(int count) {
        if (count > 30) {
            throw new PixelException(PixelExceptionCode.MAX_HISTORY_SIZE);
        }

        List<PixelUpdateHistory> histories = pixelUpdateHistoryRepository.findByOrderByCreatedAtDesc(Limit.of(count));
        return histories.stream()
                .map(PixelDrawHistoryResponse::of)
                .toList();
    }

    @Transactional(readOnly = true)
    public byte[] readMapHistory() {
        List<PixelMapHistory> mapHistories = pixelMapHistoryRepository.findAll();

        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
             DataOutputStream dos = new DataOutputStream(baos)) {

            dos.writeInt(mapHistories.size()); // 개수

            for (PixelMapHistory history : mapHistories) {
                byte[] map = history.getMap();
                dos.writeInt(map.length); // 길이
                dos.write(map);
            }

            return baos.toByteArray();

        } catch (IOException e) {
            log.error("Failed to serialize pixel map histories", e);
            throw new PixelException(PixelExceptionCode.MAP_HISTORY_ERROR);
        }
    }

    @Scheduled(initialDelayString = "#{${pixel.map-save-interval}}", fixedRateString = "#{${pixel.map-save-interval}}")
    public void saveMap() {
        if (!isMain) return;
        PixelMapHistory map = new PixelMapHistory(pixelCacheManager.getCurrentPixels());
        pixelMapHistoryRepository.save(map);
    }
}
