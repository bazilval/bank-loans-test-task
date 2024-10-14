package com.fioneer.homework.mapper;

import com.fioneer.homework.dto.loanRequest.ResponseLoanRequestDTO;
import com.fioneer.homework.dto.requestStep.ResponseRequestStepDTO;
import com.fioneer.homework.model.LoanRequest;
import com.fioneer.homework.model.RequestStep;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;

@Component
@AllArgsConstructor
public class LoanRequestMapper {

    LoanTypeMapper typeMapper;
    RequestStepMapper requestStepMapper;

    public ResponseLoanRequestDTO map(LoanRequest model) {
        Integer totalDuration = model.getSteps().stream()
                .map(RequestStep::getDuration)
                .filter(Objects::nonNull)
                .reduce(Integer::sum)
                .orElse(0);
        ResponseLoanRequestDTO dto = new ResponseLoanRequestDTO(model.getId(),
                model.getFirstName(),
                model.getLastName(),
                model.getLoanAmount(),
                model.getLoanType().getName(),
                model.getRequestStatus(),
                totalDuration,
                typeMapper.map(model.getLoanType()).getTotalExpectedDuration(),
                model.getSteps().stream()
                        .map(requestStepMapper::map)
                        .sorted(Comparator.comparing(ResponseRequestStepDTO::getOrderNum))
                        .toList(),
                model.getCreatedDate(),
                model.getModifiedDate()
        );
        return dto;
    }

    public List<ResponseLoanRequestDTO> map(List<LoanRequest> models) {
        return models.stream()
                .map(this::map)
                .toList();
    }
}
