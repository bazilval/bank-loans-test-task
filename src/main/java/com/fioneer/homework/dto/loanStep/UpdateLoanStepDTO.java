package com.fioneer.homework.dto.loanStep;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UpdateLoanStepDTO {

    private Long id;

    @Size(min = 2, max = 100, message = "Name must be less than 100 symbols")
    private String name;

    @Min(value = 1, message = "Order number must be greater than or equal to 1")
    private Integer orderNum;

    @Min(value = 1, message = "Expected duration must be greater than or equal to 1 day")
    private Integer expectedDuration;

}
