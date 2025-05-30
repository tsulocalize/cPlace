package com.cPlace.exception;

import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Log4j2
public class GlobalExceptionHandler {

    @ExceptionHandler(CPlaceException.class)
    public ResponseEntity<ExceptionResponse> handleException(CPlaceException cPlaceException) {
        CPlaceExceptionCode exceptionCode = cPlaceException.getExceptionCode();
        log.error(exceptionCode.getMessage());
        String supplementaryMessage = cPlaceException.getSupplementaryMessage();
        if (!supplementaryMessage.isBlank()) {
            log.error(supplementaryMessage);
        }

        ExceptionResponse response = new ExceptionResponse(exceptionCode.getCode(), exceptionCode.getMessage());
        return ResponseEntity.status(exceptionCode.getHttpStatus()).body(response);
    }

    public record ExceptionResponse(int code, String message) {
    }
}
