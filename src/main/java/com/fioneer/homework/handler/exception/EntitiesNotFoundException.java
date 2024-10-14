package com.fioneer.homework.handler.exception;

public class EntitiesNotFoundException extends RuntimeException {

    public EntitiesNotFoundException(String entityName) {
        super(entityName + " not found");
    }
}
