package com.fioneer.homework.controller;

import com.fioneer.homework.dto.loanType.CreateLoanTypeDTO;
import com.fioneer.homework.dto.loanType.ResponseLoanTypeDTO;
import com.fioneer.homework.dto.loanType.UpdateLoanTypeDTO;
import com.fioneer.homework.handler.FieldErrorHandler;
import com.fioneer.homework.handler.exception.BadDataException;
import com.fioneer.homework.mapper.LoanTypeMapper;
import com.fioneer.homework.model.LoanType;
import com.fioneer.homework.service.LoanStepService;
import com.fioneer.homework.service.LoanTypeService;
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
import org.springframework.web.bind.annotation.PatchMapping;
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
@RequestMapping("/loan-types")
@Tag(name = "Loan Types management", description = "Loan Types management API")
@RequiredArgsConstructor
public class LoanTypeController {

    private final LoanTypeService typeService;

    private final LoanStepService stepService;

    private final LoanTypeMapper typeMapper;

    private final Logger LOGGER = LoggerFactory.getLogger(LoanTypeController.class);

    @Operation(summary = "Get all loan types", description = "Returns a list of all loan types")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Retrieved all loan types",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ResponseLoanTypeDTO.class))}),
            @ApiResponse(responseCode = "400", description = "Invalid request", content = @Content),
            @ApiResponse(responseCode = "404", description = "Loan types not found", content = @Content)
    })
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<List<ResponseLoanTypeDTO>> getAll() {
        List<LoanType> loanTypes = typeService.getAll();

        LOGGER.info("All Loan types returned!");

        return ok().body(typeMapper.map(loanTypes));
    }

    @Operation(summary = "Search for loan types by name",
            description = "Returns a list of loan types matching the provided name")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Found the loan types",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ResponseLoanTypeDTO.class))}),
            @ApiResponse(responseCode = "400", description = "Invalid query parameter", content = @Content),
            @ApiResponse(responseCode = "404", description = "Loan types not found", content = @Content)
    })
    @GetMapping("/search")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<List<ResponseLoanTypeDTO>> getAllByNameLike(@Parameter(description = "Name of the loan type to search for")
                                                                      @RequestParam("query") String query) {
        List<LoanType> loanTypes = typeService.getAllByNameContaining(query);

        LOGGER.info("All Loan types with '" + query + "' in name returned!");

        return ok().body(typeMapper.map(loanTypes));
    }

    @Operation(summary = "Get loan type by ID", description = "Returns a single loan type by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Retrieved the loan type",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ResponseLoanTypeDTO.class))}),
            @ApiResponse(responseCode = "400", description = "Invalid ID supplied", content = @Content),
            @ApiResponse(responseCode = "404", description = "Loan type not found", content = @Content)
    })
    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<ResponseLoanTypeDTO> getById(@Parameter(description = "ID of loan type to be retrieved")
                                                       @PathVariable("id") Long id) {
        ResponseLoanTypeDTO loanType = typeMapper.map(typeService.getById(id));

        LOGGER.info("Loan type " + loanType.getName() + " with id=" + loanType.getId() + " returned!");

        return ok().body(loanType);
    }

    @Operation(summary = "Create a new loan type", description = "Creates a new loan type with steps")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Loan type created successfully",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ResponseLoanTypeDTO.class))}),
            @ApiResponse(responseCode = "400", description = "Invalid input", content = @Content),
            @ApiResponse(responseCode = "422", description = "Invalid fields", content = @Content),
            @ApiResponse(responseCode = "409", description = "Loan type with this name exists", content = @Content)
    })
    @PostMapping
    public ResponseEntity<ResponseLoanTypeDTO> create(@Parameter(description = "Loan type to be created",
                                                                    required = true)
                                                      @Valid @RequestBody CreateLoanTypeDTO dto,
                                                      BindingResult bindingResult,
                                                      UriComponentsBuilder builder) {

        try {
            stepService.validateStepsOrder(dto.getSteps());
        } catch (BadDataException e) {
            bindingResult.addError(e.toFieldError("createLoanType"));
        }

        FieldErrorHandler.handleErrors(bindingResult);

        LoanType loanType = typeService.create(dto);
        final var uri = builder.path("/loanType").pathSegment(loanType.getId().toString()).build().toUri();

        LOGGER.info("Loan type " + loanType.getName() + " with id=" + loanType.getId() + " created!");

        return created(uri).body(typeMapper.map(loanType));
    }

    @Operation(summary = "Update loan type", description = "Updates the details of an existing loan type")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Loan type updated successfully",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ResponseLoanTypeDTO.class))}),
            @ApiResponse(responseCode = "400", description = "Invalid input",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "Loan type not found",
                    content = @Content),
            @ApiResponse(responseCode = "409", description = "Conflict in updating loan type",
                    content = @Content),
            @ApiResponse(responseCode = "422", description = "Invalid fields",
                    content = @Content)
    })
    @PatchMapping("/{id}")
    public ResponseEntity<ResponseLoanTypeDTO> update(@Parameter(description = "ID of loan type to be updated")
                                                      @PathVariable Long id,
                                                      @Parameter(description = "Loan type update information",
                                                              required = true)
                                                      @Valid @RequestBody UpdateLoanTypeDTO dto,
                                                      BindingResult bindingResult) {
        FieldErrorHandler.handleErrors(bindingResult);

        LoanType updatedType = typeService.update(id, dto);

        LOGGER.info("Loan type with id=" + id + " updated!");

        return ok().body(typeMapper.map(updatedType));
    }

    @Operation(summary = "Delete loan type", description = "Deletes an existing loan type by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Loan type deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Loan type not found",
                    content = @Content),
            @ApiResponse(responseCode = "409", description = "Conflict in deleting loan type",
                    content = @Content)
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@Parameter(description = "ID of loan type to be deleted")
                                       @PathVariable("id") Long id) {
        typeService.delete(id);

        LOGGER.info("Loan type with id=" + id + " deleted!");

        return noContent().build();
    }
}

