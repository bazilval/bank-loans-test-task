package com.fioneer.homework.model;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.fioneer.homework.model.status.StepStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;

@Getter
@Setter
@ToString
@NoArgsConstructor
@Entity
@EqualsAndHashCode
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
@EntityListeners(AuditingEntityListener.class)
public class RequestStep {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Min(value = 1, message = "Actual duration must be be greater than or equal to 1 day")
    @Column(name = "actual_duration")
    private Integer duration;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "loan_request_id", nullable = false)
    private LoanRequest loanRequest;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "loan_step_id", nullable = false)
    private LoanStep loanStep;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "step_status", columnDefinition = "TEXT")
    private StepStatus status = StepStatus.PENDING;

    @LastModifiedDate
    @Column(nullable = false)
    private Instant modifiedDate;
}
