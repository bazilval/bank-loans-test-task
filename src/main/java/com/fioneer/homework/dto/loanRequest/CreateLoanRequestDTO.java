package com.fioneer.homework.dto.loanRequest;

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
public class CreateLoanRequestDTO {

    @NotBlank(message = "Firstname is required field")
    @Size(min = 1, max = 100, message = "Firstname must be more than 1 and less than 100 symbols")
    private String firstName;

    @NotBlank(message = "Lastname is required field")
    @Size(min = 1, max = 100, message = "Lastname must be more than 1 and less than 100 symbols")
    private String lastName;

    @NotNull(message = "LoanAmount is required field")
    @Min(value = 100, message = "Loan amount must be be greater than or equal to 100")
    private Integer loanAmount;

    @NotNull(message = "Loan type is required field")
    private Long loanTypeId;

}
