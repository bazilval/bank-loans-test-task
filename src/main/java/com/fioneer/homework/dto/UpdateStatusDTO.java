package com.fioneer.homework.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateStatusDTO {

    @NotNull(message = "Status is required field")
    private String status;
    @NotNull(message = "Duration is required field")
    @Min(value = 1, message = "Duration must be greater than or equal to 1")
    private Integer duration;

}
