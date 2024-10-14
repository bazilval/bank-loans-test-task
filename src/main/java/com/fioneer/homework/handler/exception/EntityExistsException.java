package com.fioneer.homework.handler.exception;

import org.springframework.http.ProblemDetail;
import org.springframework.web.ErrorResponseException;

import static java.text.MessageFormat.format;
import static org.springframework.http.HttpStatus.BAD_REQUEST;

public class EntityExistsException extends ErrorResponseException {

    private static final String MESSAGE_TEMPLATE = "{0} with {1} ''{2}'' already exists {3}";

    private final String fieldName;

    private final String errorValue;

    public EntityExistsException(String entityName, String fieldName, String errorValue) {
        this(entityName, fieldName, errorValue, "");
    }

    public EntityExistsException(String entityName, String fieldName, String errorValue, String postfix) {
        super(BAD_REQUEST,
                ProblemDetail.forStatusAndDetail(BAD_REQUEST,
                        entityName + " with " + fieldName + " '" + errorValue + "' already exists"),
                null,
                format(MESSAGE_TEMPLATE, entityName, fieldName, errorValue, postfix),
                new Object[]{fieldName, errorValue});
        this.fieldName = fieldName;
        this.errorValue = errorValue;
    }
}
