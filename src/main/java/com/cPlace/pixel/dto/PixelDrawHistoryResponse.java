package com.cPlace.pixel.dto;

import com.cPlace.pixel.domain.PixelUpdateHistory;
import java.time.LocalDateTime;

public record PixelDrawHistoryResponse(
        int x,
        int y,
        String color,
        LocalDateTime drawAt,
        Long maskedMemberId
) {
    public static PixelDrawHistoryResponse of(PixelUpdateHistory history) {
        return new PixelDrawHistoryResponse(
                history.getPositionX(),
                history.getPositionY(),
                history.getColor().name(),
                history.getCreatedAt(),
                history.getChzzkMemberId()
        );
    }
}
