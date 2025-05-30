package com.cPlace.chzzk.exception;

import com.cPlace.exception.CPlaceException;
import com.cPlace.exception.CPlaceExceptionCode;

public class ChzzkException extends CPlaceException {


    public ChzzkException(CPlaceExceptionCode exceptionCode) {
        super(exceptionCode);
    }

    public ChzzkException(CPlaceExceptionCode exceptionCode, String supplementaryMessage) {
        super(exceptionCode, supplementaryMessage);
    }
}
