package com.fioneer.homework.dto.loanStep;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ResponseLoanStepDTO {
    private Long id;
    private final String name;
    private final Integer orderNum;
    private final Integer expectedDuration;
}
