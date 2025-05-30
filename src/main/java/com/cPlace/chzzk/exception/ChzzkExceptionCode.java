package com.cPlace.chzzk.exception;

import com.cPlace.exception.CPlaceExceptionCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ChzzkExceptionCode implements CPlaceExceptionCode {

    // 1_xxx
    CHZZK_SERVER_EXCEPTION("치지직 서버 통신에 문제가 발생했습니다.", 1_001, HttpStatus.INTERNAL_SERVER_ERROR),

    // 2_xxx
    MEMBER_ALREADY_EXIST("이미 등록된 유저입니다.", 2_001, HttpStatus.BAD_REQUEST),
    MEMBER_NOT_EXIST("등록되지 않은 유저입니다.", 2_002, HttpStatus.BAD_REQUEST),
    COOKIE_NOT_FOUND("필요한 쿠키가 존재하지 않습니다.", 2_003, HttpStatus.BAD_REQUEST),
    UNAUTHORIZED("허가되지 않은 요청입니다.", 2_004, HttpStatus.UNAUTHORIZED),
    ;

    private final String message;
    private final int code;
    private final HttpStatus httpStatus;
}
