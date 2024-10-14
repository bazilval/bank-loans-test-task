package com.fioneer.homework.mapper;

import com.fioneer.homework.dto.requestStep.ResponseRequestStepDTO;
import com.fioneer.homework.model.RequestStep;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class RequestStepMapper {

    public ResponseRequestStepDTO map(RequestStep model) {
        return new ResponseRequestStepDTO(model.getId(),
                model.getLoanStep().getOrderNum(),
                model.getLoanStep().getName(),
                model.getDuration(),
                model.getLoanStep().getExpectedDuration(),
                model.getStatus(),
                model.getModifiedDate());
    }
}
