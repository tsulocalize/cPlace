package com.cPlace.pixel.service;

import com.cPlace.chzzk.domain.ChzzkMember;
import com.cPlace.chzzk.respository.ChzzkMemberRepository;
import com.cPlace.pixel.config.PixelProperties;
import com.cPlace.pixel.domain.Color;
import com.cPlace.pixel.domain.Pixel;
import com.cPlace.pixel.domain.PixelUpdateHistory;
import com.cPlace.pixel.exception.PixelException;
import com.cPlace.pixel.exception.PixelExceptionCode;
import com.cPlace.pixel.repository.PixelUpdateHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.Clock;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Service
@RequiredArgsConstructor
public class PixelService {

    private final Clock clock;
    private final PixelUpdateHistoryRepository pixelUpdateHistoryRepository;
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
        pixelUpdateHistoryRepository.save(new PixelUpdateHistory(pixel, chzzkMember, now));
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
        LocalDateTime availableFrom = lastActiveAt.plusSeconds(pixelProperties.getDrawingTimeLimit());
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
}
