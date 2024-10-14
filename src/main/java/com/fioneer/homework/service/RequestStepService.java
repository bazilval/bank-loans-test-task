package com.fioneer.homework.service;

import com.fioneer.homework.dto.UpdateStatusDTO;
import com.fioneer.homework.handler.exception.EntityNotFoundByFieldException;
import com.fioneer.homework.handler.exception.EntityNotFoundException;
import com.fioneer.homework.model.LoanRequest;
import com.fioneer.homework.model.LoanStep;
import com.fioneer.homework.model.RequestStep;
import com.fioneer.homework.repository.RequestStepRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RequestStepService {

    private final RequestStepRepository requestStepRepository;
    private final StatusUpdateService statusUpdateService;

    Logger LOGGER = LoggerFactory.getLogger(RequestStepService.class);
    private final String entityName = "Request step";

    @Transactional(readOnly = true)
    public RequestStep getById(Long id) {
        RequestStep step = requestStepRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(entityName, id));

        return step;
    }

    @Transactional(readOnly = true)
    public RequestStep getByOrderNumAndRequestId(Integer orderNum, Long requestId) {
        RequestStep step = requestStepRepository.findByLoanStepOrderNumAndLoanRequestId(orderNum, requestId)
                .orElseThrow(() -> new EntityNotFoundByFieldException(entityName,
                        "order num and request ID", orderNum + " and " + requestId));

        return step;
    }

    @Transactional
    public RequestStep create(final LoanStep loanStep, final LoanRequest request) {
        RequestStep step = new RequestStep();
        step.setLoanRequest(request);
        step.setLoanStep(loanStep);

        requestStepRepository.save(step);
        LOGGER.info("Request step with id=" + step.getId() + " created");
        return step;
    }

    @Transactional
    public void updateStatus(RequestStep step, UpdateStatusDTO statusDTO) {
        step.setDuration(statusDTO.getDuration());

        statusUpdateService.processStatusUpdate(step.getLoanRequest(), step, statusDTO);
    }
}
