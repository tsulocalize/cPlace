package com.cPlace.pixel.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

public record PixelDrawRequest(
        @Positive int x,
        @Positive int y,
        @NotBlank String color) {
}
