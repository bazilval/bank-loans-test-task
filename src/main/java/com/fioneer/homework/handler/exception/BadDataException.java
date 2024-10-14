package com.fioneer.homework.handler.exception;

import org.springframework.http.ProblemDetail;
import org.springframework.validation.FieldError;
import org.springframework.web.ErrorResponseException;

import static org.springframework.http.HttpStatus.UNPROCESSABLE_ENTITY;

public class BadDataException extends ErrorResponseException {

    private final String fieldName;
    private final String errorValue;

    public BadDataException(String fieldName, String errorValue) {
        super(UNPROCESSABLE_ENTITY, ProblemDetail.forStatusAndDetail(UNPROCESSABLE_ENTITY, errorValue), null,
                errorValue,
                new Object[]{fieldName, errorValue});
        this.fieldName = fieldName;
        this.errorValue = errorValue;
    }

    public FieldError toFieldError(String objectName) {
        return new FieldError(
                objectName,
                fieldName,
                errorValue,
                false,
                null,
                null,
                errorValue
        );
    }
}
