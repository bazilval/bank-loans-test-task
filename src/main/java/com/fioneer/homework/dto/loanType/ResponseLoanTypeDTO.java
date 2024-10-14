package com.fioneer.homework.dto.loanType;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fioneer.homework.dto.loanStep.ResponseLoanStepDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class ResponseLoanTypeDTO {
    private Long id;

    private String name;

    @JsonManagedReference
    private List<ResponseLoanStepDTO> steps;

    private Integer totalExpectedDuration;
}
