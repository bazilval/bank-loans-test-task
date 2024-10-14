package com.fioneer.homework.handler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fioneer.homework.handler.exception.BadDataException;
import lombok.AllArgsConstructor;
import org.springframework.validation.BindingResult;

import java.util.List;
import java.util.stream.Collectors;

@AllArgsConstructor
public class FieldErrorHandler {

    public static void handleErrors(BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            String errorMessage = collectMessages(bindingResult);
            throw new BadDataException("Fields errors", errorMessage);
        }
    }

    private static String collectMessages(BindingResult bindingResult) {
        ObjectMapper objectMapper = new ObjectMapper();

        List<String> errors = bindingResult.getFieldErrors().stream()
                .map(error -> error.getField() + "\": \"" + error.getDefaultMessage() + ",")
                .collect(Collectors.toList());
        try {
            return objectMapper.writeValueAsString(errors);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return "";
        }
    }
}
