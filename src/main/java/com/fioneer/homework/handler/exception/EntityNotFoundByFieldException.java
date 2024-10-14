package com.fioneer.homework.handler.exception;

public class EntityNotFoundByFieldException extends RuntimeException {
    public EntityNotFoundByFieldException(String entityName, String fieldName, String value) {
        super(entityName + " with " + fieldName + " '" + value + "' not found!");
    }
}
