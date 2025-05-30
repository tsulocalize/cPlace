package com.cPlace.admin.service;

import com.cPlace.admin.dto.CoverPixelRequest;
import com.cPlace.admin.dto.PatchMemberRequest;
import com.cPlace.chzzk.domain.ChzzkMember;
import com.cPlace.chzzk.exception.ChzzkException;
import com.cPlace.chzzk.exception.ChzzkExceptionCode;
import com.cPlace.chzzk.respository.ChzzkMemberRepository;
import com.cPlace.pixel.config.PixelProperties;
import com.cPlace.pixel.domain.Color;
import com.cPlace.pixel.domain.Pixel;
import com.cPlace.pixel.exception.PixelException;
import com.cPlace.pixel.exception.PixelExceptionCode;
import com.cPlace.pixel.service.PixelCacheManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.Clock;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final ChzzkMemberRepository chzzkMemberRepository;
    private final PixelProperties pixelProperties;
    private final PixelCacheManager pixelCacheManager;
    private final Clock clock;

    @Transactional
    public ChzzkMember changeMemberState(PatchMemberRequest request) {
        if (request.ban()) {
            return banMember(request.channelName());
        }
        return unbanMember(request.channelName());
    }

    private ChzzkMember banMember(String channelName) {
        ChzzkMember member = chzzkMemberRepository.findByChannelName(channelName)
                .orElseThrow(() -> new ChzzkException(ChzzkExceptionCode.MEMBER_NOT_EXIST));
        member.ban();
        return chzzkMemberRepository.save(member);
    }

    private ChzzkMember unbanMember(String channelName) {
        ChzzkMember member = chzzkMemberRepository.findByChannelName(channelName)
                .orElseThrow(() -> new ChzzkException(ChzzkExceptionCode.MEMBER_NOT_EXIST));
        member.unban();
        return chzzkMemberRepository.save(member);
    }

    @Transactional
    public void coverPixel(CoverPixelRequest request) {
        validateCoordinate(request);
        List<Pixel> pixels = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now(clock);
        Color color = Color.findColor(request.color());

        IntStream.rangeClosed(request.startX(), request.endX())
                .forEach(x -> IntStream.rangeClosed(request.startY(), request.endY())
                        .forEach(y -> pixels.add(new Pixel(x, y, color, now.toEpochSecond(ZoneOffset.UTC)))));

        pixelCacheManager.addDirtySet(pixels);
    }

    private void validateCoordinate(CoverPixelRequest request) {
        if (request.startX() < 0 || request.startY() < 0) {
            throw new PixelException(PixelExceptionCode.PIXEL_NOT_FOUND);
        }
        if (request.endX() >= pixelProperties.getPixelSize() || request.endY() >= pixelProperties.getPixelSize()) {
            throw new PixelException(PixelExceptionCode.PIXEL_NOT_FOUND);
        }
    }
}
