package com.fioneer.homework.handler.exception;

public class EntityDeleteUpdateException extends RuntimeException {
    public EntityDeleteUpdateException(String entityName, Long id, String explanation) {
        super(entityName + " with id=" + id + " can not be deleted or updated. " + explanation);
    }
}
