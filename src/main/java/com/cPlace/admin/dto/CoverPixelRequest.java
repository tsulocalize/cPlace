package com.cPlace.admin.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

public record CoverPixelRequest(
        @Positive int startX,
        @Positive int startY,
        @Positive int endX,
        @Positive int endY,
        @NotBlank String color) {

    public static CoverPixelRequest of(int startX, int startY, int endX, int endY, String color) {
        return new CoverPixelRequest(startX, startY, endX, endY, color);
    }
}
