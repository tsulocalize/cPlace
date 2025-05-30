package com.cPlace.pixel.dto;

import jakarta.validation.constraints.NotBlank;

public record PixelDrawRequest(int x, int y, @NotBlank String color) {
}
