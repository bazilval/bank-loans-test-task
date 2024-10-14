package com.fioneer.homework.repository;

import com.fioneer.homework.model.LoanType;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LoanTypeRepository extends JpaRepository<LoanType, Long> {
    @EntityGraph(attributePaths = {"steps"})
    LoanType queryById(Long id);

    Optional<LoanType> findByNameIgnoreCase(String name);

    List<LoanType> findByNameContainingIgnoreCase(String name);

    boolean existsByName(String name);


}
