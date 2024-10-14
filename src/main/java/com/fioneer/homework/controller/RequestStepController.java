package com.fioneer.homework.controller;

import com.fioneer.homework.dto.UpdateStatusDTO;
import com.fioneer.homework.dto.requestStep.ResponseRequestStepDTO;
import com.fioneer.homework.mapper.RequestStepMapper;
import com.fioneer.homework.model.RequestStep;
import com.fioneer.homework.service.RequestStepService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.ResponseEntity.ok;

@RestController
@RequestMapping("/loan-requests/{requestId}/steps")
@Tag(name = "Request Steps management", description = "Request Steps management API")
@RequiredArgsConstructor
public class RequestStepController {

    private final RequestStepService requestStepService;

    private final RequestStepMapper stepMapper;

    private final Logger LOGGER = LoggerFactory.getLogger(RequestStepController.class);


    @Operation(summary = "Get request step by requestId and orderNum",
            description = "Returns a single request step by requestID and its orderNum")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Retrieved the request step",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ResponseRequestStepDTO.class)) }),
            @ApiResponse(responseCode = "400", description = "Invalid ID supplied", content = @Content),
            @ApiResponse(responseCode = "404", description = "Request step not found", content = @Content)
    })

    // OrderNum and requestID using to identify step instead of requestStepID
    // It seems more intuitive and right to work with order numbers to retrieve step or to update step status

    @GetMapping("/{orderNum}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<ResponseRequestStepDTO> getById(@Parameter(description = "Order num of request step to be retrieved")
                                                          @PathVariable("orderNum") Integer orderNum,
                                                          @Parameter(description = "Id of loan request which step retrieved")
                                                          @PathVariable("requestId") Long requestId) {
        RequestStep step = requestStepService.getByOrderNumAndRequestId(orderNum, requestId);

        LOGGER.info("Request step with id=" + step.getId() + " returned!");

        return ok().body(stepMapper.map(step));
    }

    @Operation(summary = "Update status of request step",
            description = "Update status and actual duration of existing request step")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Status updated",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ResponseRequestStepDTO.class)) }),
            @ApiResponse(responseCode = "400", description = "Invalid ID or orderNum supplied", content = @Content),
            @ApiResponse(responseCode = "404", description = "Request step not found", content = @Content)
    })
    @PatchMapping("/{orderNum}/status")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<ResponseRequestStepDTO> updateStatus(@Parameter(description = "Order num of request step to be updated")
                                                                   @PathVariable("orderNum") Integer orderNum,
                                                               @Parameter(description = "Id of loan request which step updated")
                                                                   @PathVariable("requestId") Long requestId,
                                                               @Parameter(description = "Status to be assigned",
                                                                        required = true)
                                                                   @Valid @RequestBody UpdateStatusDTO statusDTO
                                                               ) {
        RequestStep step = requestStepService.getByOrderNumAndRequestId(orderNum, requestId);
        requestStepService.updateStatus(step, statusDTO);

        LOGGER.info("Loan request with id=" + step.getId() + " returned!");
        return ok().body(stepMapper.map(step));
    }
}
