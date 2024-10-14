package com.fioneer.homework.handler;

import com.fioneer.homework.handler.exception.BadDataException;
import com.fioneer.homework.handler.exception.EntitiesNotFoundException;
import com.fioneer.homework.handler.exception.EntityDeleteUpdateException;
import com.fioneer.homework.handler.exception.EntityExistsException;
import com.fioneer.homework.handler.exception.EntityNotFoundByFieldException;
import com.fioneer.homework.handler.exception.EntityNotFoundException;
import lombok.NoArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
@NoArgsConstructor
public class CommonExceptionHandler extends ResponseEntityExceptionHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(CommonExceptionHandler.class);


    @ExceptionHandler
    public ResponseEntity<Object> handleException(EntityNotFoundException e) {
        LOGGER.error(e.getMessage());
        return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler
    public ResponseEntity<Object> handleException(EntityNotFoundByFieldException e) {
        LOGGER.error(e.getMessage());
        return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler
    public ResponseEntity<Object> handleException(EntitiesNotFoundException e) {
        LOGGER.error(e.getMessage());
        return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler
    public ResponseEntity<Object> handleException(EntityExistsException e) {
        LOGGER.error(e.getMessage());
        return new ResponseEntity<>(e.getMessage(), HttpStatus.CONFLICT);
    }

    @ExceptionHandler
    public ResponseEntity<Object> handleException(BadDataException e) {
        LOGGER.error(e.getMessage());
        return new ResponseEntity<>(e.getMessage(), HttpStatus.UNPROCESSABLE_ENTITY);
    }

    @ExceptionHandler
    public ResponseEntity<Object> handleException(EntityDeleteUpdateException e) {
        LOGGER.error(e.getMessage());
        return new ResponseEntity<>(e.getMessage(), HttpStatus.CONFLICT);
    }
}
