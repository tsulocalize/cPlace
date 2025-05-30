package com.cPlace.pixel.exception;

import com.cPlace.exception.CPlaceExceptionCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
@Getter
public enum PixelExceptionCode implements CPlaceExceptionCode {

    // 1_xxx
    PIXEL_NOT_FOUND("해당 위치에 픽셀이 없습니다", 1_001, HttpStatus.NOT_FOUND),
    COLOR_NOT_EXIST("존재하지 않는 색상입니다", 1_002, HttpStatus.BAD_REQUEST),
    TIME_LIMITED("아직 색상을 변경할 수 없습니다", 1_003, HttpStatus.BAD_REQUEST),
    AUTHORIZATION_INVALID("허가되지 않은 행동입니다", 1_004, HttpStatus.UNAUTHORIZED),
    ;

    private final String message;
    private final int code;
    private final HttpStatus httpStatus;
}
