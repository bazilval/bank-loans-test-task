package com.fioneer.homework.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fioneer.homework.dto.loanRequest.CreateLoanRequestDTO;
import com.fioneer.homework.dto.loanStep.CreateLoanStepDTO;
import com.fioneer.homework.dto.loanType.CreateLoanTypeDTO;
import com.fioneer.homework.model.LoanRequest;
import com.fioneer.homework.model.status.RequestStatus;
import com.fioneer.homework.repository.LoanRequestRepository;
import com.fioneer.homework.repository.LoanStepRepository;
import com.fioneer.homework.repository.LoanTypeRepository;
import com.fioneer.homework.repository.RequestStepRepository;
import com.fioneer.homework.service.LoanRequestService;
import com.fioneer.homework.service.LoanTypeService;
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

import static net.javacrumbs.jsonunit.assertj.JsonAssertions.assertThatJson;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class LoanRequestControllerTest {
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
    private ObjectMapper mapper;

    private final String route = "/loan-requests";

    @BeforeAll
    public void beforeAll() throws Exception {
        CreateLoanStepDTO step1DTO = new CreateLoanStepDTO("Test step 1", 1, 3);
        CreateLoanStepDTO step2DTO = new CreateLoanStepDTO("Test step 2", 2, 6);
        CreateLoanStepDTO step3DTO = new CreateLoanStepDTO("Test step 3", 3, 9);

        List<CreateLoanStepDTO> stepDTOs = new ArrayList<>();
        stepDTOs.add(step1DTO);
        stepDTOs.add(step2DTO);
        stepDTOs.add(step3DTO);

        CreateLoanTypeDTO typeDTO = new CreateLoanTypeDTO("Test type 1", stepDTOs);
        typeService.create(typeDTO);
    }

    @AfterAll
    public void afterAll() throws Exception {
        typeRepository.deleteAll();
        stepRepository.deleteAll();
        requestRepository.deleteAll();
        requestStepRepository.deleteAll();
    }

    @BeforeEach
    public void beforeEach() throws Exception {
        requestRepository.deleteAll();
        requestStepRepository.deleteAll();

        CreateLoanRequestDTO requestDTO = new CreateLoanRequestDTO("Test firstname", "Test lastname", 5000, 1L);
        requestService.create(requestDTO);
    }

    @Test
    public void testGetAll() throws Exception {
        var request = get(route);

        MvcResult result = mockMvc.perform(request)
                .andExpect(status().isOk())
                .andReturn();

        String body = result.getResponse().getContentAsString();
        assertThatJson(body).isArray();
        assertTrue(body.contains("Test firstname"));
    }

    @Test
    public void testGetAllByStatus() throws Exception {
        var request = get(route + "/search")
                .queryParam("status", "processing");

        MvcResult result = mockMvc.perform(request)
                .andExpect(status().isOk())
                .andReturn();

        String body = result.getResponse().getContentAsString();
        assertTrue(body.contains("Test firstname"));
    }

    @Test
    public void testGetById() throws Exception {
        Long id = 1L;
        var request = get(route + "/" + id);

        MvcResult result = mockMvc.perform(request)
                .andExpect(status().isOk())
                .andReturn();

        String body = result.getResponse().getContentAsString();
        assertTrue(body.contains("Test firstname"));
    }

    @Test
    public void testCreate() throws Exception {
        var firstName = "Test firstname 2";
        var lastName = "Test lastName 2";
        CreateLoanRequestDTO requestDTO = new CreateLoanRequestDTO(firstName, lastName, 5000, 1L);

        var request = post(route)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(requestDTO));

        mockMvc.perform(request).andExpect(status().isCreated());

        LoanRequest createdRequest = requestRepository.findById(2L).get();

        assertNotNull(createdRequest);
        assertEquals(requestDTO.getFirstName(), createdRequest.getFirstName());
        assertEquals(RequestStatus.PROCESSING, createdRequest.getRequestStatus());
    }

    @Test
    public void testCreateWrongName() throws Exception {
        var firstName = "";
        var lastName = "Test lastName 2";
        CreateLoanRequestDTO requestDTO = new CreateLoanRequestDTO(firstName, lastName, 5000, 1L);

        var request = post(route)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(requestDTO));

        mockMvc.perform(request).andExpect(status().isUnprocessableEntity());

        assertFalse(requestRepository.findById(2L).isPresent());
    }

    @Test
    public void testCreateWithNoExistingType() throws Exception {
        var firstName = "Test firstname 2";
        var lastName = "Test lastName 2";
        CreateLoanRequestDTO requestDTO = new CreateLoanRequestDTO(firstName, lastName, 5000, 2L);

        var request = post(route)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(requestDTO));

        mockMvc.perform(request).andExpect(status().isNotFound());

        assertFalse(requestRepository.findById(2L).isPresent());
    }

    @Test
    public void testDelete() throws Exception {
        Long id = 1L;
        var request = delete(route + "/" + id);

        mockMvc.perform(request).andExpect(status().isNoContent());

        assertTrue(requestRepository.findById(id).isEmpty());
    }
}
