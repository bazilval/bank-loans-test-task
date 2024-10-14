package com.fioneer.homework.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fioneer.homework.dto.loanRequest.CreateLoanRequestDTO;
import com.fioneer.homework.dto.loanStep.CreateLoanStepDTO;
import com.fioneer.homework.dto.loanStep.UpdateLoanStepDTO;
import com.fioneer.homework.dto.loanType.CreateLoanTypeDTO;
import com.fioneer.homework.dto.loanType.UpdateLoanTypeDTO;
import com.fioneer.homework.model.LoanType;
import com.fioneer.homework.repository.LoanRequestRepository;
import com.fioneer.homework.repository.LoanStepRepository;
import com.fioneer.homework.repository.LoanTypeRepository;
import com.fioneer.homework.service.LoanRequestService;
import com.fioneer.homework.service.LoanTypeService;
import org.junit.jupiter.api.AfterAll;
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
import java.util.stream.Collectors;

import static net.javacrumbs.jsonunit.assertj.JsonAssertions.assertThatJson;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class LoanTypeControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private LoanTypeRepository typeRepository;
    @Autowired
    private LoanStepRepository stepRepository;
    @Autowired
    private LoanRequestRepository requestRepository;
    @Autowired
    private LoanTypeService typeService;
    @Autowired
    private LoanRequestService requestService;
    @Autowired
    private ObjectMapper mapper;

    private final String route = "/loan-types";

    @BeforeEach
    public void beforeEach() throws Exception {
        typeRepository.deleteAll();
        stepRepository.deleteAll();
        requestRepository.deleteAll();

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
    public void afterAll() {
        typeRepository.deleteAll();
        stepRepository.deleteAll();
        requestRepository.deleteAll();
    }

    @Test
    public void testGetAll() throws Exception {
        var request = get(route);

        MvcResult result = mockMvc.perform(request)
                .andExpect(status().isOk())
                .andReturn();

        String body = result.getResponse().getContentAsString();
        assertThatJson(body).isArray();
        assertTrue(body.contains("Test type 1"));
    }

    @Test
    public void testGetAllByNameLike() throws Exception {
        var request = get(route + "/search")
                .queryParam("query", "te");

        MvcResult result = mockMvc.perform(request)
                .andExpect(status().isOk())
                .andReturn();

        String body = result.getResponse().getContentAsString();
        assertTrue(body.contains("Test type"));
    }

    @Test
    public void testGetById() throws Exception {
        Long id = 1L;
        var request = get(route + "/" + id);

        MvcResult result = mockMvc.perform(request)
                .andExpect(status().isOk())
                .andReturn();

        String body = result.getResponse().getContentAsString();
        assertTrue(body.contains("Test type"));
        assertTrue(body.contains("Test step 2"));
    }

    @Test
    public void testCreate() throws Exception {
        List<String> stepNames = List.of("Test step 1", "Test step 2", "Test step 3");

        CreateLoanStepDTO step1DTO = new CreateLoanStepDTO(stepNames.get(0), 1, 3);
        CreateLoanStepDTO step2DTO = new CreateLoanStepDTO(stepNames.get(1), 2, 6);
        CreateLoanStepDTO step3DTO = new CreateLoanStepDTO(stepNames.get(2), 3, 9);

        List<CreateLoanStepDTO> stepDTOs = List.of(step1DTO, step2DTO, step3DTO);

        var name = "Test type 2";
        CreateLoanTypeDTO typeDTO = new CreateLoanTypeDTO(name, stepDTOs);

        var request = post(route)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(typeDTO));

        mockMvc.perform(request).andExpect(status().isCreated());

        LoanType type = typeRepository.findByNameIgnoreCase(name).get();

        assertNotNull(type);
        assertEquals(typeDTO.getName(), type.getName());

        type.getSteps().stream()
                .peek(step -> assertTrue(stepNames.contains(step.getName())));
    }

    @Test
    public void testCreateWrongName() throws Exception {
        List<String> stepNames = List.of("Test step 1", "Test step 2", "Test step 3");

        CreateLoanStepDTO step1DTO = new CreateLoanStepDTO(stepNames.get(0), 1, 3);
        CreateLoanStepDTO step2DTO = new CreateLoanStepDTO(stepNames.get(1), 2, 6);
        CreateLoanStepDTO step3DTO = new CreateLoanStepDTO(stepNames.get(2), 3, 9);

        List<CreateLoanStepDTO> stepDTOs = List.of(step1DTO, step2DTO, step3DTO);

        var name = "";
        CreateLoanTypeDTO typeDTO = new CreateLoanTypeDTO(name, stepDTOs);

        var request = post(route)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(typeDTO));

        mockMvc.perform(request).andExpect(status().isUnprocessableEntity());

        assertFalse(typeRepository.findByNameIgnoreCase(name).isPresent());
    }

    @Test
    public void testCreateExistingName() throws Exception {
        List<String> stepNames = List.of("Test step 1", "Test step 2", "Test step 3");

        CreateLoanStepDTO step1DTO = new CreateLoanStepDTO(stepNames.get(0), 1, 3);
        CreateLoanStepDTO step2DTO = new CreateLoanStepDTO(stepNames.get(1), 2, 6);
        CreateLoanStepDTO step3DTO = new CreateLoanStepDTO(stepNames.get(2), 3, 9);

        List<CreateLoanStepDTO> stepDTOs = List.of(step1DTO, step2DTO, step3DTO);

        var name = "Test type 1";
        CreateLoanTypeDTO typeDTO = new CreateLoanTypeDTO(name, stepDTOs);

        var request = post(route)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(typeDTO));

        mockMvc.perform(request).andExpect(status().isConflict());
        assertEquals(1, typeService.getAll().size());
    }

    @Test
    public void testCreateWrongSteps() throws Exception {
        List<String> stepNames = List.of("Test step 1", "", "Test step 3");

        CreateLoanStepDTO step1DTO = new CreateLoanStepDTO(stepNames.get(0), -8, 3);
        CreateLoanStepDTO step2DTO = new CreateLoanStepDTO(stepNames.get(1), 2, 6);
        CreateLoanStepDTO step3DTO = new CreateLoanStepDTO(stepNames.get(2), 3, 0);

        List<CreateLoanStepDTO> stepDTOs = List.of(step1DTO, step2DTO, step3DTO);

        var name = "Test type 2";
        CreateLoanTypeDTO typeDTO = new CreateLoanTypeDTO(name, stepDTOs);

        var request = post(route)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(typeDTO));

        mockMvc.perform(request).andExpect(status().isUnprocessableEntity());

        assertFalse(typeRepository.findByNameIgnoreCase(name).isPresent());
    }

    @Test
    public void testUpdateNameAndStepsNames() throws Exception {
        Long id = 1L;
        LoanType type = typeRepository.queryById(id);

        List<UpdateLoanStepDTO> stepDTOs = type.getSteps().stream()
                .map(step -> new UpdateLoanStepDTO(step.getId(), step.getName() + " upd", null, null))
                .toList();

        var name = "Test type 2";
        UpdateLoanTypeDTO typeDTO = new UpdateLoanTypeDTO(name, stepDTOs, null);

        var request = patch(route + "/" + id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(typeDTO));

        mockMvc.perform(request).andExpect(status().isOk());

        LoanType updatedType = typeService.getByNameIgnoreCase(name);

        updatedType.getSteps().stream()
                .peek(step -> assertTrue(step.getName().endsWith("upd")));
    }

    @Test
    public void testUpdateRearrangeSteps() throws Exception {
        long id = 1L;
        LoanType type = typeRepository.queryById(id);

        List<UpdateLoanStepDTO> stepDTOs = type.getSteps().stream()
                .map(step -> new UpdateLoanStepDTO(step.getId(), null, step.getOrderNum() + 1, null))
                .collect(Collectors.toList());
        stepDTOs.add(new UpdateLoanStepDTO(null, "New step", 1, 3));

        UpdateLoanTypeDTO typeDTO = new UpdateLoanTypeDTO(null, stepDTOs, null);

        var request = patch(route + "/" + id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(typeDTO));

        mockMvc.perform(request).andExpect(status().isOk());

        LoanType updatedType = typeRepository.queryById(id);

        assertEquals(4, updatedType.getSteps().size());
    }

    @Test
    public void testUpdateDeleteSteps() throws Exception {
        Long id = 1L;
        LoanType type = typeRepository.findById(id).get();

        UpdateLoanTypeDTO typeDTO = new UpdateLoanTypeDTO(null, null, List.of(2L, 3L));

        var request = patch(route + "/" + id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(typeDTO));

        mockMvc.perform(request).andExpect(status().isOk());

        LoanType updatedType = typeRepository.queryById(id);

        assertEquals(1, updatedType.getSteps().size());
    }

    @Test
    public void testUpdateDeleteFirstStep() throws Exception {
        Long id = 1L;
        LoanType type = typeRepository.findById(id).get();

        UpdateLoanTypeDTO typeDTO = new UpdateLoanTypeDTO(null, null, List.of(1L));

        var request = patch(route + "/" + id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(typeDTO));

        mockMvc.perform(request).andExpect(status().isUnprocessableEntity());

        LoanType updatedType = typeRepository.queryById(id);

        assertEquals(3, updatedType.getSteps().size());
    }

    @Test
    public void testUpdateWithExistingName() throws Exception {
        CreateLoanStepDTO step1DTO = new CreateLoanStepDTO("Test step 1", 1, 3);

        List<CreateLoanStepDTO> stepDTOs = new ArrayList<>();
        stepDTOs.add(step1DTO);

        String newName = "Test type 2";

        CreateLoanTypeDTO createTypeDTO = new CreateLoanTypeDTO(newName, stepDTOs);
        typeService.create(createTypeDTO);

        Long id = 1L;

        UpdateLoanTypeDTO typeDTO = new UpdateLoanTypeDTO(newName, null, null);

        var request = patch(route + "/" + id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(typeDTO));

        mockMvc.perform(request).andExpect(status().isConflict());

        LoanType updatedType = typeService.getById(id);

        assertNotEquals(newName, updatedType.getName());
    }

    // TODO
    @Test
    public void testUpdateWithExistingRequests() throws Exception {
        Long id = 1L;

        CreateLoanRequestDTO requestDTO = new CreateLoanRequestDTO("Test firstname", "Test lastname", 5000, id);
        requestService.create(requestDTO);

        String newName = "Test type 2";

        UpdateLoanTypeDTO typeDTO = new UpdateLoanTypeDTO(newName, null, null);

        var request = patch(route + "/" + id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(typeDTO));

        mockMvc.perform(request).andExpect(status().isConflict());

        assertNotEquals(typeRepository.findById(id).get().getName(), newName);
    }

    @Test
    public void testDelete() throws Exception {
        Long id = 1L;
        var request = delete(route + "/" + id);

        mockMvc.perform(request).andExpect(status().isNoContent());

        assertTrue(typeRepository.findById(id).isEmpty());
        assertTrue(stepRepository.findById(id).isEmpty());
    }

    // TODO
    @Test
    public void testDeleteWithExistingRequests() throws Exception {
        Long id = 1L;

        CreateLoanRequestDTO requestDTO = new CreateLoanRequestDTO("Test firstname", "Test lastname", 5000, id);
        requestService.create(requestDTO);
        var request = delete(route + "/" + id);

        mockMvc.perform(request).andExpect(status().isConflict());

        assertFalse(typeRepository.findById(id).isEmpty());
    }
}
