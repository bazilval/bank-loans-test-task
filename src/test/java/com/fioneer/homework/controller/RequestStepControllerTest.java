package com.fioneer.homework.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fioneer.homework.dto.UpdateStatusDTO;
import com.fioneer.homework.dto.loanRequest.CreateLoanRequestDTO;
import com.fioneer.homework.dto.loanStep.CreateLoanStepDTO;
import com.fioneer.homework.dto.loanType.CreateLoanTypeDTO;
import com.fioneer.homework.model.LoanRequest;
import com.fioneer.homework.model.RequestStep;
import com.fioneer.homework.model.status.RequestStatus;
import com.fioneer.homework.model.status.StepStatus;
import com.fioneer.homework.repository.LoanRequestRepository;
import com.fioneer.homework.repository.LoanStepRepository;
import com.fioneer.homework.repository.LoanTypeRepository;
import com.fioneer.homework.repository.RequestStepRepository;
import com.fioneer.homework.service.LoanRequestService;
import com.fioneer.homework.service.LoanTypeService;
import com.fioneer.homework.service.RequestStepService;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class RequestStepControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private LoanTypeRepository typeRepository;
    @Autowired
    private LoanStepRepository stepRepository;
    @Autowired
    private LoanRequestRepository requestRepository;
    @Autowired
    private RequestStepRepository requestStepRepository;
    @Autowired
    private LoanTypeService typeService;
    @Autowired
    private LoanRequestService requestService;
    @Autowired
    private RequestStepService requestStepService;
    @Autowired
    private ObjectMapper mapper;

    private final String route = "/loan-requests/%d/steps/%d";

    @BeforeAll
    public void beforeAll() throws Exception {
        CreateLoanStepDTO step1DTO = new CreateLoanStepDTO("Test step 1", 1, 3);
        CreateLoanStepDTO step2DTO = new CreateLoanStepDTO("Test step 2", 2, 6);

        List<CreateLoanStepDTO> stepDTOs = new ArrayList<>();
        stepDTOs.add(step1DTO);
        stepDTOs.add(step2DTO);

        CreateLoanTypeDTO typeDTO = new CreateLoanTypeDTO("Test type 1", stepDTOs);
        typeService.create(typeDTO);
    }

    @AfterAll
    public void afterAll() throws Exception {
        requestRepository.deleteAll();
        requestStepRepository.deleteAll();
        typeRepository.deleteAll();
        stepRepository.deleteAll();
    }

    @BeforeEach
    public void beforeEach() throws Exception {
        requestRepository.deleteAll();
        requestStepRepository.deleteAll();

        CreateLoanRequestDTO requestDTO = new CreateLoanRequestDTO("Test firstname", "Test lastname", 5000, 1L);
        requestService.create(requestDTO);
    }

    @Test
    public void testGetByOrderNumAndRequestId() throws Exception {
        long id = 1L;
        int orderNum = 1;

        var request = get(String.format(route, id, orderNum));

        MvcResult result = mockMvc.perform(request).andExpect(status().isOk()).andReturn();

        String body = result.getResponse().getContentAsString();
        assertTrue(body.contains("Test step 1"));
    }

    @Test
    public void testUpdateStepStatus() throws Exception {
        long id = 1L;
        int orderNum = 1;

        UpdateStatusDTO statusDTO = new UpdateStatusDTO("successful", 5);

        var request = patch(String.format(route, id, orderNum) + "/status").contentType(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(statusDTO));

        mockMvc.perform(request).andExpect(status().isOk());

        RequestStep updatedStep = requestStepService.getByOrderNumAndRequestId(orderNum, id);

        assertEquals(StepStatus.SUCCESSFUL, updatedStep.getStatus());
    }

    @Test
    public void testUpdateAllStatuses() throws Exception {
        long id = 1L;
        int orderNum = 1;

        UpdateStatusDTO statusDTO = new UpdateStatusDTO("successful", 5);

        var request = patch(String.format(route, id, orderNum) + "/status").contentType(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(statusDTO));

        mockMvc.perform(request).andExpect(status().isOk());

        request = patch(String.format(route, id, orderNum + 1) + "/status").contentType(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(statusDTO));

        mockMvc.perform(request).andExpect(status().isOk());

        RequestStep updatedStep = requestStepService.getByOrderNumAndRequestId(orderNum, id);
        assertEquals(StepStatus.SUCCESSFUL, updatedStep.getStatus());
        updatedStep = requestStepService.getByOrderNumAndRequestId(orderNum + 1, id);
        assertEquals(StepStatus.SUCCESSFUL, updatedStep.getStatus());

        LoanRequest loanRequest = requestService.getById(1L);
        assertEquals(RequestStatus.APPROVED, loanRequest.getRequestStatus());
    }

    @Test
    public void testUpdateStepStatusOfFailedRequest() throws Exception {
        long id = 1L;
        int orderNum = 1;

        UpdateStatusDTO statusDTO = new UpdateStatusDTO("failed", 5);

        var request = patch(String.format(route, id, orderNum) + "/status").contentType(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(statusDTO));

        mockMvc.perform(request).andExpect(status().isOk());

        LoanRequest loanRequest = requestService.getById(1L);
        assertEquals(RequestStatus.REJECTED, loanRequest.getRequestStatus());

        request = patch(String.format(route, id, orderNum) + "/status").contentType(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(statusDTO));

        mockMvc.perform(request).andExpect(status().isConflict());
    }

    @Test
    public void testUpdateStepStatusToFailed() throws Exception {
        long id = 1L;
        int orderNum = 1;

        UpdateStatusDTO statusDTO = new UpdateStatusDTO("failed", 5);

        var request = patch(String.format(route, id, orderNum) + "/status").contentType(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(statusDTO));

        mockMvc.perform(request).andExpect(status().isOk());

        RequestStep updatedStep = requestStepService.getByOrderNumAndRequestId(orderNum, id);
        assertEquals(StepStatus.FAILED, updatedStep.getStatus());
        updatedStep = requestStepService.getByOrderNumAndRequestId(orderNum + 1, id);
        assertEquals(StepStatus.FAILED, updatedStep.getStatus());

        LoanRequest loanRequest = requestService.getById(1L);
        assertEquals(RequestStatus.REJECTED, loanRequest.getRequestStatus());
    }

    @Test
    public void testUpdateWrongStatus() throws Exception {
        long id = 1L;
        int orderNum = 2;

        UpdateStatusDTO statusDTO = new UpdateStatusDTO("successful", 5);

        var request = patch(String.format(route, id, orderNum) + "/status").contentType(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(statusDTO));

        mockMvc.perform(request).andExpect(status().isConflict());

        RequestStep updatedStep = requestStepService.getByOrderNumAndRequestId(orderNum, id);

        assertNotEquals(StepStatus.SUCCESSFUL, updatedStep.getStatus());
    }
}
