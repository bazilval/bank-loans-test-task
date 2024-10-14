package com.fioneer.homework.repository;

import com.fioneer.homework.model.RequestStep;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RequestStepRepository extends JpaRepository<RequestStep, Long> {
    Optional<RequestStep> findByLoanStepOrderNumAndLoanRequestId(Integer orderNum, Long id);
}
