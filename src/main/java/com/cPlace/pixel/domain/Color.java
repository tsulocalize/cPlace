package com.cPlace.pixel.domain;

import com.cPlace.pixel.exception.PixelException;
import com.cPlace.pixel.exception.PixelExceptionCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import java.util.Arrays;

@RequiredArgsConstructor
@Getter
public enum Color {
//    Primary Colors:
    RED("#EE2222"),
    BLUE("#2222EE"),
    YELLOW("#EEEE22"),

//    Secondary Colors:
    GREEN("#228022"),
    ORANGE("#FFA522"),
    PURPLE("#802280"),

//    Neutral Colors:
    BLACK("#000000"),
    WHITE("#FFFFFF"),
    LIGHT_GRAY("#D3D3D3"),
    DARK_GRAY("#A9A9A9"),
    BROWN("#8B4513"),

//    Accent or Tertiary Colors:
    CYAN("#22EEEE"),
    MAGENTA("#EE22EE"),
    LIME("#22EE22"),

//    Additional Warmth/Coolness:
    SKIN("#F4D9CB"),
    CORAL("#FF7F50")
    ;

    private final String colorCode;

    public static Color findColor(String colorCode) {
        return Arrays.stream(Color.values())
                .filter(color -> color.colorCode.equals(colorCode))
                .findFirst()
                .orElseThrow(() -> new PixelException(PixelExceptionCode.COLOR_NOT_EXIST, colorCode));
    }
}
