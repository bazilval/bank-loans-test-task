package com.fioneer.homework.dto.loanRequest;

import com.fioneer.homework.dto.requestStep.ResponseRequestStepDTO;
import com.fioneer.homework.model.status.RequestStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.List;


@Getter
@Setter
@AllArgsConstructor
public class ResponseLoanRequestDTO {

    private final Long id;

    private final String firstName;

    private final String lastName;

    private final Integer loanAmount;

    private final String loanType;

    private final RequestStatus requestStatus;

    private final Integer duration;

    private final Integer expectedDuration;

    private final List<ResponseRequestStepDTO> steps;

    private final Instant createdDate;

    private final Instant modifiedDate;
}
