package com.fioneer.homework.service;

import com.fioneer.homework.dto.loanRequest.CreateLoanRequestDTO;
import com.fioneer.homework.handler.exception.EntitiesNotFoundException;
import com.fioneer.homework.handler.exception.EntityNotFoundByFieldException;
import com.fioneer.homework.handler.exception.EntityNotFoundException;
import com.fioneer.homework.model.LoanRequest;
import com.fioneer.homework.model.LoanType;
import com.fioneer.homework.model.RequestStep;
import com.fioneer.homework.model.status.RequestStatus;
import com.fioneer.homework.repository.LoanRequestRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LoanRequestService {

    private final LoanRequestRepository requestRepository;
    private final LoanTypeService typeService;
    private final RequestStepService requestStepService;

    Logger LOGGER = LoggerFactory.getLogger(LoanRequestService.class);
    private final String entityName = "Loan request";

    @Transactional(readOnly = true)
    public List<LoanRequest> getAll() {
        List<LoanRequest> requests = requestRepository.findAll();

        if (requests.isEmpty()) {
            throw new EntitiesNotFoundException(entityName);
        }

        return requests;
    }

    @Transactional(readOnly = true)
    public List<LoanRequest> getAllByStatus(final String status) {

        RequestStatus requestStatus;
        try {
            requestStatus = RequestStatus.valueOf(status.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new EntityNotFoundByFieldException("Request status", "name", status);
        }

        List<LoanRequest> requests = requestRepository.findByRequestStatus(requestStatus);

        if (requests.isEmpty()) {
            throw new EntitiesNotFoundException(entityName);
        }

        return requests;
    }

    @Transactional(readOnly = true)
    public LoanRequest getById(final Long id) {
        LoanRequest request = requestRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(entityName, id));

        return request;
    }

    @Transactional
    public LoanRequest create(final CreateLoanRequestDTO dto) {
        LoanRequest request = new LoanRequest();
        request.setFirstName(dto.getFirstName());
        request.setLastName(dto.getLastName());
        request.setLoanAmount(dto.getLoanAmount());

        LoanType type = typeService.getById(dto.getLoanTypeId());
        request.setLoanType(type);

        requestRepository.save(request);

        List<RequestStep> requestSteps = type.getSteps().stream()
                .map(step -> requestStepService.create(step, request))
                .collect(Collectors.toList());

        request.setSteps(requestSteps);
        return request;
    }

    @Transactional
    public void delete(final Long id) {
        LoanRequest request = requestRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(entityName, id));

        requestRepository.deleteById(id);
    }
}
