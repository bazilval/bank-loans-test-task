package com.fioneer.homework.controller;

import com.fioneer.homework.dto.loanRequest.CreateLoanRequestDTO;
import com.fioneer.homework.dto.loanRequest.ResponseLoanRequestDTO;
import com.fioneer.homework.handler.FieldErrorHandler;
import com.fioneer.homework.mapper.LoanRequestMapper;
import com.fioneer.homework.model.LoanRequest;
import com.fioneer.homework.service.LoanRequestService;
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
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

import static org.springframework.http.ResponseEntity.created;
import static org.springframework.http.ResponseEntity.noContent;
import static org.springframework.http.ResponseEntity.ok;

@RestController
@RequestMapping("/loan-requests")
@Tag(name = "Loan Requests management", description = "Loan Requests management API")
@RequiredArgsConstructor
public class LoanRequestController {

    private final LoanRequestService requestService;
    private final LoanRequestMapper requestMapper;

    private final Logger LOGGER = LoggerFactory.getLogger(LoanRequestController.class);

    @Operation(summary = "Get all loan requests", description = "Returns a list of all loan requests")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Retrieved all loan requests",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ResponseLoanRequestDTO.class))}),
            @ApiResponse(responseCode = "400", description = "Invalid request", content = @Content),
            @ApiResponse(responseCode = "404", description = "Loan requests not found", content = @Content)
    })
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<List<ResponseLoanRequestDTO>> getAll() {
        List<LoanRequest> loanRequests = requestService.getAll();

        LOGGER.info("All Loan requests returned!");

        return ok().body(requestMapper.map(loanRequests));
    }

    @Operation(summary = "Search for loan requests by status",
            description = "Returns a list of loan requests matching the provided status")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Found the loan requests",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ResponseLoanRequestDTO.class))}),
            @ApiResponse(responseCode = "400", description = "Invalid query parameter", content = @Content),
            @ApiResponse(responseCode = "404", description = "Loan requests not found", content = @Content)
    })
    @GetMapping("/search")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<List<ResponseLoanRequestDTO>> getAllByStatus(@Parameter(description = "Status of the loan request to search for")
                                                                       @RequestParam("status") String status) {
        List<LoanRequest> loanRequests = requestService.getAllByStatus(status);

        LOGGER.info("All Loan requests with status '" + status + "' returned!");

        return ok().body(requestMapper.map(loanRequests));
    }

    @Operation(summary = "Get loan request by ID", description = "Returns a single loan request by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Retrieved the loan request",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ResponseLoanRequestDTO.class))}),
            @ApiResponse(responseCode = "400", description = "Invalid ID supplied", content = @Content),
            @ApiResponse(responseCode = "404", description = "Loan request not found", content = @Content)
    })
    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<ResponseLoanRequestDTO> getById(@Parameter(description = "ID of loan request to be retrieved")
                                                          @PathVariable("id") Long id) {
        LoanRequest loanRequest = requestService.getById(id);

        LOGGER.info("Loan request with id=" + loanRequest.getId() + " returned!");

        return ok().body(requestMapper.map(loanRequest));
    }

    @Operation(summary = "Create a new loan request", description = "Creates a new loan request")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Loan request created successfully",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ResponseLoanRequestDTO.class))}),
            @ApiResponse(responseCode = "400", description = "Invalid input", content = @Content),
            @ApiResponse(responseCode = "422", description = "Invalid fields", content = @Content)
    })
    @PostMapping
    public ResponseEntity<ResponseLoanRequestDTO> create(@Parameter(description = "Loan request to be created",
                                                                    required = true)
                                                         @Valid @RequestBody CreateLoanRequestDTO dto,
                                                         BindingResult bindingResult,
                                                         UriComponentsBuilder builder) {

        FieldErrorHandler.handleErrors(bindingResult);

        LoanRequest loanRequest = requestService.create(dto);
        final var uri = builder.path("/loanType").pathSegment(loanRequest.getId().toString()).build().toUri();

        LOGGER.info("Loan request with id=" + loanRequest.getId() + " created!");

        return created(uri).body(requestMapper.map(loanRequest));
    }

    @Operation(summary = "Delete loan request", description = "Deletes an existing loan request by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Loan request deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Loan request not found",
                    content = @Content)
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@Parameter(description = "ID of loan request to be deleted")
                                       @PathVariable("id") Long id) {
        requestService.delete(id);

        LOGGER.info("Loan request with id=" + id + " deleted!");

        return noContent().build();
    }
}
