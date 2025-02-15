package com.fioneer.homework.dto.loanType;

import com.fioneer.homework.dto.loanStep.CreateLoanStepDTO;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CreateLoanTypeDTO {

    @NotBlank(message = "Name is required field")
    @Size(min = 2, max = 100, message = "Name must be more than 1 and less than 100 symbols")
    private String name;

    @NotEmpty(message = "Steps cannot be empty")
    @Valid
    private List<CreateLoanStepDTO> steps;

}
