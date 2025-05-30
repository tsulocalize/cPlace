package com.cPlace.exception;

import org.springframework.http.HttpStatus;

public interface CPlaceExceptionCode {

    String getMessage();

    int getCode();

    HttpStatus getHttpStatus();
}
