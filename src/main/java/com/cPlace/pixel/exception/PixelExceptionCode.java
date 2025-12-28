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

    // 2_xxx
    MAX_HISTORY_SIZE("최대 호출 크기를 초과했습니다", 2_001, HttpStatus.BAD_REQUEST),
    MAP_HISTORY_ERROR("맵 히스토리를 조회하는데 실패했습니다", 2_002, HttpStatus.INTERNAL_SERVER_ERROR),
    ;

    private final String message;
    private final int code;
    private final HttpStatus httpStatus;
}
