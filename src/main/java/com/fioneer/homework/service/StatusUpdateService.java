package com.fioneer.homework.service;

import com.fioneer.homework.dto.UpdateStatusDTO;
import com.fioneer.homework.handler.exception.EntityDeleteUpdateException;
import com.fioneer.homework.handler.exception.EntityNotFoundByFieldException;
import com.fioneer.homework.model.LoanRequest;
import com.fioneer.homework.model.RequestStep;
import com.fioneer.homework.model.status.RequestStatus;
import com.fioneer.homework.model.status.StepStatus;
import com.fioneer.homework.repository.LoanRequestRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StatusUpdateService {
    private final LoanRequestRepository requestRepository;

    Logger LOGGER = LoggerFactory.getLogger(LoanRequestRepository.class);

    @Transactional
    public void processStatusUpdate(LoanRequest loanRequest, RequestStep step, UpdateStatusDTO statusDTO) {
        checkUpdatePossibility(loanRequest, step);

        StepStatus stepStatus = getStepStatus(statusDTO);
        Integer orderNumToUpdate = step.getLoanStep().getOrderNum();
        List<RequestStep> steps = loanRequest.getSteps();

        step.setStatus(stepStatus);
        LOGGER.info("Request with id=" + step.getId() + " changed status to '" + stepStatus.name() + "'");

        if (stepStatus.equals(StepStatus.FAILED)) {
            handleFailedStep(loanRequest, steps, orderNumToUpdate);
            LOGGER.info("Loan request with id=" + loanRequest.getId() + " changed status to 'REJECTED'");
        } else if (orderNumToUpdate == steps.size()) {
            loanRequest.setRequestStatus(RequestStatus.APPROVED);
            LOGGER.info("Loan request with id=" + loanRequest.getId() + " changed status to 'APPROVED'");
        }

        requestRepository.save(loanRequest);
    }

    void checkUpdatePossibility(LoanRequest loanRequest, RequestStep step) {
        Integer orderNumToUpdate = step.getLoanStep().getOrderNum();
        List<RequestStep> steps = loanRequest.getSteps();

        if (!loanRequest.getRequestStatus().equals(RequestStatus.PROCESSING)) {
            throw new EntityDeleteUpdateException("Request step", step.getId(),
                    "Loan Request of this step is not in 'PROCESSING' status.");
        }

        if (!step.getStatus().equals(StepStatus.PENDING)) {
            throw new EntityDeleteUpdateException("Request step", step.getId(),
                    "Step is not in 'PENDING' status.");
        }

        boolean hasPendingStepsBefore = steps.stream()
                .anyMatch(requestStep -> (requestStep.getLoanStep().getOrderNum() < orderNumToUpdate)
                        && requestStep.getStatus().equals(StepStatus.PENDING));

        if (hasPendingStepsBefore) {
            throw new EntityDeleteUpdateException("Request step", step.getId(),
                    "There is at least one step with less order num and 'PENDING' status");
        }
    }

    StepStatus getStepStatus(UpdateStatusDTO statusDTO) {
        try {
            return StepStatus.valueOf(statusDTO.getStatus().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new EntityNotFoundByFieldException("Step status", "name", statusDTO.getStatus());
        }
    }

    void handleFailedStep(LoanRequest loanRequest, List<RequestStep> steps, int failedStepOrderNum) {
        loanRequest.setRequestStatus(RequestStatus.REJECTED);

        // Set all steps after failed one to 'failed' state too, because them definitely not 'pending' anymore
        // Maybe it's better to have one more status for them, e.g. 'blocked'
        for (var requestStep : steps) {
            if (requestStep.getLoanStep().getOrderNum() > failedStepOrderNum) {
                requestStep.setStatus(StepStatus.FAILED);
            }
        }
    }

}
