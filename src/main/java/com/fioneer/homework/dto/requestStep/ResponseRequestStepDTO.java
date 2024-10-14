package com.fioneer.homework.dto.requestStep;

import com.fioneer.homework.model.status.StepStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.Instant;

@Getter
@AllArgsConstructor
public class ResponseRequestStepDTO {

    private final Long id;
    private final Integer orderNum;
    private final String stepName;
    private final Integer duration;
    private final Integer expectedDuration;
    private final StepStatus stepStatus;
    private final Instant modifiedDate;
}
