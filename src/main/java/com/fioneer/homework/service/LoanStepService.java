package com.fioneer.homework.service;

import com.fioneer.homework.dto.loanStep.CreateLoanStepDTO;
import com.fioneer.homework.dto.loanStep.UpdateLoanStepDTO;
import com.fioneer.homework.handler.exception.BadDataException;
import com.fioneer.homework.handler.exception.EntityNotFoundException;
import com.fioneer.homework.model.LoanStep;
import com.fioneer.homework.model.LoanType;
import com.fioneer.homework.repository.LoanStepRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LoanStepService {

    private final LoanStepRepository stepRepository;

    Logger LOGGER = LoggerFactory.getLogger(LoanStepService.class);

    @Transactional(readOnly = true)
    public List<LoanStep> getAllByLoanTypeId(final Long id) {
        return stepRepository.findAllByLoanType_Id(id);
    }

    @Transactional
    public LoanStep create(final CreateLoanStepDTO dto, final LoanType loanType) {
        if (dto.getName() == null || dto.getOrderNum() == null || dto.getExpectedDuration() == null) {
            throw new BadDataException("New loan step", "All fields in loan steps have not to be null");
        }

        LoanStep loanStep = new LoanStep(dto.getName(), dto.getOrderNum(), dto.getExpectedDuration(), loanType);

        stepRepository.save(loanStep);
        LOGGER.info("Loan step with id=" + loanStep.getId() + " created");
        return loanStep;
    }

    @Transactional
    public LoanStep update(final Long id, final UpdateLoanStepDTO dto, final LoanType loanType) {
        LoanStep step = stepRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Loan step", id));

        if (dto.getName() != null) {
            step.setName(dto.getName());
        }
        if (dto.getOrderNum() != null) {
            step.setOrderNum(dto.getOrderNum());
        }
        if (dto.getExpectedDuration() != null) {
            step.setExpectedDuration(dto.getExpectedDuration());
        }

        stepRepository.save(step);
        LOGGER.info("Loan step with id=" + id + " updated");

        return step;
    }

    @Transactional
    public void deleteByIds(final Long loanTypeId, final List<Long> idsToDelete) {
        List<LoanStep> steps = stepRepository.findAllByLoanType_Id(loanTypeId);

        List<Long> oldIds = steps.stream().map(LoanStep::getId).toList();
        if (idsToDelete.containsAll(oldIds)) {
            throw new BadDataException("Steps to delete", "You cannot delete all steps of loan type");
        }

        steps.stream()
                .map(LoanStep::getId)
                .filter(idsToDelete::contains)
                .peek(stepRepository::deleteById)
                .peek(id -> LOGGER.info("Step with id=" + id + " deleted!"));
    }

    public void validateStepsOrder(final List<CreateLoanStepDTO> steps) {
        List<Integer> orderNumbers = steps.stream()
                .map(CreateLoanStepDTO::getOrderNum)
                .sorted()
                .toList();

        for (int i = 0; i < orderNumbers.size(); i++) {
            if (orderNumbers.get(i) != i + 1) {
                throw new BadDataException("Step order",
                        "Step order numbers must start from 1 and be continuous without gaps");
            }
        }
    }
}
