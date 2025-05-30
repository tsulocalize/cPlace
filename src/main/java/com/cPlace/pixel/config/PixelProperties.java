package com.cPlace.pixel.config;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Getter
public class PixelProperties {

    @Value("#{${pixel.drawing-time-limit}}")
    private int drawingTimeLimit;

    @Value("${pixel.size:1000}")
    private int pixelSize;

}
