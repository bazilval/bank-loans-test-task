package com.fioneer.homework.mapper;

import com.fioneer.homework.dto.loanStep.ResponseLoanStepDTO;
import com.fioneer.homework.dto.loanType.ResponseLoanTypeDTO;
import com.fioneer.homework.model.LoanStep;
import com.fioneer.homework.model.LoanType;
import com.fioneer.homework.repository.LoanTypeRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;

@Component
@AllArgsConstructor
public class LoanTypeMapper {

    LoanStepMapper stepMapper;
    LoanTypeRepository repository;

    public ResponseLoanTypeDTO map(LoanType model) {
        Integer totalDuration = model.getSteps().stream()
                .map(LoanStep::getExpectedDuration)
                .reduce(Integer::sum)
                .orElse(0);
        ResponseLoanTypeDTO dto = new ResponseLoanTypeDTO(model.getId(),
                model.getName(),
                model.getSteps().stream()
                        .map(step -> new ResponseLoanStepDTO(step.getId(),
                                                            step.getName(),
                                                            step.getOrderNum(),
                                                            step.getExpectedDuration()))
                        .sorted(Comparator.comparing(ResponseLoanStepDTO::getOrderNum)).toList(),
                totalDuration);

        return dto;
    }

    public List<ResponseLoanTypeDTO> map(List<LoanType> models) {
        return models.stream()
                .map(this::map)
                .toList();
    }
}
