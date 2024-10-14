package com.fioneer.homework.repository;

import com.fioneer.homework.model.LoanStep;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LoanStepRepository extends JpaRepository<LoanStep, Long> {
    List<LoanStep> findAllByLoanType_Id(Long id);
}
