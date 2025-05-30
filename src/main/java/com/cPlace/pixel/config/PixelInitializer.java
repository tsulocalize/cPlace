package com.cPlace.pixel.config;

import com.cPlace.pixel.domain.Color;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.redisson.api.RBitSet;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Log4j2
public class PixelInitializer implements ApplicationRunner {

    private final RedissonClient redissonClient;

    @Value("${pixel.size:1000}")
    private int pixelSize;

    @Override
    public void run(ApplicationArguments args) {
        RBitSet bitSet = redissonClient.getBitSet("canvas_bit");
        if (bitSet.isExists()) {
            log.info("Redis pixels are already initialized");
            return;
        }

        for (long i = 0; i < pixelSize; i++) {
            for (long j = 0; j < pixelSize; j++) {
                bitSet.setByteAsync(8 * (i + pixelSize * j), (byte) Color.WHITE.ordinal());
            }
        }

        RBucket<String> lastUpdated = redissonClient.getBucket("last_updated");
        if (!lastUpdated.isExists()) {
            lastUpdated.set("0");
        }

        log.info("Redis pixels are initialized with size: {}", pixelSize);
    }
}
