package com.cPlace.exception;

import lombok.Getter;

@Getter
public class CPlaceException extends RuntimeException {

    private final CPlaceExceptionCode exceptionCode;
    private final String supplementaryMessage;

    public CPlaceException(CPlaceExceptionCode exceptionCode) {
        this(exceptionCode, "");
    }

    public CPlaceException(CPlaceExceptionCode exceptionCode, String supplementaryMessage) {
        super(exceptionCode.getMessage());
        this.exceptionCode = exceptionCode;
        this.supplementaryMessage = supplementaryMessage;
    }
}
