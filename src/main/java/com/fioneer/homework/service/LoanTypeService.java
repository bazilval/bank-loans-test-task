package com.fioneer.homework.service;

import com.fioneer.homework.dto.loanStep.UpdateLoanStepDTO;
import com.fioneer.homework.dto.loanType.CreateLoanTypeDTO;
import com.fioneer.homework.dto.loanType.UpdateLoanTypeDTO;
import com.fioneer.homework.handler.exception.BadDataException;
import com.fioneer.homework.handler.exception.EntitiesNotFoundException;
import com.fioneer.homework.handler.exception.EntityDeleteUpdateException;
import com.fioneer.homework.handler.exception.EntityExistsException;
import com.fioneer.homework.handler.exception.EntityNotFoundByFieldException;
import com.fioneer.homework.handler.exception.EntityNotFoundException;
import com.fioneer.homework.mapper.LoanStepMapper;
import com.fioneer.homework.mapper.LoanTypeMapper;
import com.fioneer.homework.model.LoanStep;
import com.fioneer.homework.model.LoanType;
import com.fioneer.homework.repository.LoanTypeRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LoanTypeService {

    private final LoanTypeRepository typeRepository;
    private final LoanStepService stepService;
    private final LoanTypeMapper typeMapper;
    private final LoanStepMapper stepMapper;

    Logger LOGGER = LoggerFactory.getLogger(LoanTypeService.class);

    private final String entityName = "Loan Type";

    @Transactional(readOnly = true)
    public List<LoanType> getAll() {
        List<LoanType> loanTypes = typeRepository.findAll();

        if (loanTypes.isEmpty()) {
            throw new EntitiesNotFoundException(entityName);
        }

        return loanTypes;
    }

    @Transactional(readOnly = true)
    public List<LoanType> getAllByNameContaining(final String name) {
        List<LoanType> loanTypes = typeRepository.findByNameContainingIgnoreCase(name);

        if (loanTypes.isEmpty()) {
            throw new EntitiesNotFoundException(entityName);
        }

        return loanTypes;
    }

    @Transactional(readOnly = true)
    public LoanType getById(final Long id) {
        LoanType loanType = typeRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(entityName, id));

        return loanType;
    }

    @Transactional(readOnly = true)
    public LoanType getByNameIgnoreCase(final String name) {
        LoanType loanType = typeRepository.findByNameIgnoreCase(name)
                .orElseThrow(() -> new EntityNotFoundByFieldException(entityName, "name", name));

        return loanType;
    }

    @Transactional
    public LoanType create(final CreateLoanTypeDTO dto) {
        if (typeRepository.existsByName(dto.getName())) {
            throw new EntityExistsException("Loan Type", "name", dto.getName());
        }

        LoanType loanType = new LoanType();
        loanType.setName(dto.getName());
        typeRepository.save(loanType);

        List<LoanStep> loanSteps = dto.getSteps().stream()
                .map(stepDTO -> stepService.create(stepDTO, loanType))
                .collect(Collectors.toList());

        loanType.setSteps(loanSteps);
        return loanType;
    }

    @Transactional
    public LoanType update(final Long id, final UpdateLoanTypeDTO dto) {
        LoanType loanType = typeRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(entityName, id));

        checkIfRequestsExists(id);

        String newName = dto.getName();
        if (newName != null && !newName.equals(loanType.getName())) {
            if (typeRepository.existsByName(newName)) {
                throw new EntityExistsException(entityName, "name", newName);
            }

            loanType.setName(newName);
        }

        List<Long> idsToDelete = dto.getStepsIdsToDelete();
        List<LoanStep> oldSteps = loanType.getSteps();

        if (idsToDelete != null && !idsToDelete.isEmpty()) {
            stepService.deleteByIds(id, idsToDelete);
            oldSteps.removeIf(step -> idsToDelete.contains(step.getId()));
        }

        if (dto.getSteps() != null) {
            List<UpdateLoanStepDTO> updateStepDTOs = dto.getSteps();

            Map<Long, LoanStep> currentStepsMap = oldSteps.stream()
                    .collect(Collectors.toMap(LoanStep::getId, Function.identity()));

            for (UpdateLoanStepDTO stepDTO : updateStepDTOs) {
                Long updateId = stepDTO.getId();

                if (updateId != null && !currentStepsMap.containsKey(updateId)) {
                    throw new BadDataException("Updating steps",
                            "You can only update steps that belongs to Loan Type with id=" + id);
                }

                // if step has ID that means we have to update existing step
                if (updateId != null) {
                    LoanStep updatedStep = stepService.update(updateId, stepDTO, loanType);

                    oldSteps.removeIf(step -> Objects.equals(step.getId(), updateId));
                    oldSteps.add(updatedStep);

                    // if step doesn't have ID that means we have to add new step
                } else {
                    LoanStep newStep = stepService.create(stepMapper.map(stepDTO), loanType);
                    oldSteps.add(newStep);
                }
            }
        }

        stepService.validateStepsOrder(stepMapper.map(oldSteps));
        loanType.setSteps(oldSteps);

        typeRepository.save(loanType);
        return loanType;
    }

    @Transactional
    public void delete(final Long id) {
        LoanType loanType = typeRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(entityName, id));

        checkIfRequestsExists(id);

        typeRepository.delete(loanType);
    }

    public void checkIfRequestsExists(Long id) {
        LoanType loanType = typeRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(entityName, id));

        if (loanType.getRequests() != null && !loanType.getRequests().isEmpty()) {
            throw new EntityDeleteUpdateException("Loan Type", id, "There is at least one loan request with this type");
        }
    }
}
