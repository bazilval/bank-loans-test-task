package com.fioneer.homework.mapper;

import com.fioneer.homework.dto.loanStep.CreateLoanStepDTO;
import com.fioneer.homework.dto.loanStep.UpdateLoanStepDTO;
import com.fioneer.homework.model.LoanStep;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class LoanStepMapper {
    public CreateLoanStepDTO map(LoanStep model) {
        return new CreateLoanStepDTO(model.getName(), model.getOrderNum(), model.getExpectedDuration());
    }

    public CreateLoanStepDTO map(UpdateLoanStepDTO dto) {
        return new CreateLoanStepDTO(dto.getName(), dto.getOrderNum(), dto.getExpectedDuration());
    }

    public List<CreateLoanStepDTO> map(List<LoanStep> models) {
        return models.stream()
                .map(model -> map(model))
                .toList();
    }
}
