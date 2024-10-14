package com.fioneer.homework.repository;

import com.fioneer.homework.model.LoanRequest;
import com.fioneer.homework.model.status.RequestStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LoanRequestRepository extends JpaRepository<LoanRequest, Long> {
    List<LoanRequest> findByRequestStatus(RequestStatus requestStatus);
}
