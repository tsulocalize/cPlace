package com.cPlace.pixel.exception;

import com.cPlace.exception.CPlaceException;
import com.cPlace.exception.CPlaceExceptionCode;

public class PixelException extends CPlaceException {

    public PixelException(PixelExceptionCode exceptionCode) {
        super(exceptionCode);
    }

    public PixelException(CPlaceExceptionCode exceptionCode, String supplementaryMessage) {
        super(exceptionCode, supplementaryMessage);
    }
}
