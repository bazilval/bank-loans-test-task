package com.fioneer.homework.dto.loanStep;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CreateLoanStepDTO {

    @NotBlank(message = "Name is required field")
    @Size(max = 100, message = "Name must be less than 100 symbols")
    private String name;

    @NotNull(message = "Order Num is required field")
    @Min(value = 1, message = "Order number must be greater than or equal to 1")
    private Integer orderNum;

    @NotNull(message = "Expected duration is required field")
    @Min(value = 1, message = "Expected duration must be greater than or equal to 1 day")
    private Integer expectedDuration;

}
