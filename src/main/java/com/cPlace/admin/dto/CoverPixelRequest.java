package com.cPlace.admin.dto;

import jakarta.validation.constraints.NotBlank;

public record CoverPixelRequest(int startX, int startY, int endX, int endY, @NotBlank String color) {

    public static CoverPixelRequest of(int startX, int startY, int endX, int endY, String color) {
        return new CoverPixelRequest(startX, startY, endX, endY, color);
    }
}
