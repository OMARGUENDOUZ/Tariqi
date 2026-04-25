package com.example.carly.controller;

import com.example.carly.dto.examstudent.ExamStudentCreateRequest;
import com.example.carly.dto.examstudent.ExamStudentResponse;
import com.example.carly.exception.ExamRegistrationException;
import com.example.carly.exception.ResourceNotFoundException;
import com.example.carly.mapper.ExamStudentMapper;
import com.example.carly.model.*;
import com.example.carly.security.JwtAuthFilter;
import com.example.carly.service.ExamService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = ExamController.class,
        excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = JwtAuthFilter.class))
class ExamControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ExamService examService;

    @MockBean
    private ExamStudentMapper examStudentMapper;

    private ExamStudentResponse buildResponse(long id) {
        return new ExamStudentResponse(id, 1L, 10L, ExamCategory.CODE, ExamStatus.PLANNED, ExamResult.PENDING);
    }

    @Test
    @WithMockUser
    void findAll_returnsEmptyList_200() throws Exception {
        when(examService.findWithFilter(any())).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/v1/exam-students"))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
    }

    @Test
    @WithMockUser
    void findById_found_returns200() throws Exception {
        ExamStudent es = new ExamStudent();
        es.setId(1L);
        when(examService.findById(1L)).thenReturn(Optional.of(es));
        when(examStudentMapper.toResponse(es)).thenReturn(buildResponse(1L));

        mockMvc.perform(get("/api/v1/exam-students/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    @WithMockUser
    void findById_notFound_returns404() throws Exception {
        when(examService.findById(99L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/v1/exam-students/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser
    void save_validBody_returns201() throws Exception {
        ExamStudentCreateRequest req = new ExamStudentCreateRequest(1L, 10L, ExamCategory.CODE);
        ExamStudent saved = new ExamStudent();
        saved.setId(5L);
        when(examService.registerStudentForExam(1L, 10L, ExamCategory.CODE)).thenReturn(saved);
        when(examStudentMapper.toResponse(saved)).thenReturn(buildResponse(5L));

        mockMvc.perform(post("/api/v1/exam-students")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(jsonPath("$.id").value(5));
    }

    @Test
    @WithMockUser
    void save_slotFull_returns409() throws Exception {
        ExamStudentCreateRequest req = new ExamStudentCreateRequest(1L, 10L, ExamCategory.CODE);
        when(examService.registerStudentForExam(anyLong(), anyLong(), any()))
                .thenThrow(new ExamRegistrationException("Exam slot is full"));

        mockMvc.perform(post("/api/v1/exam-students")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isConflict());
    }

    @Test
    @WithMockUser
    void delete_success_returns204() throws Exception {
        mockMvc.perform(delete("/api/v1/exam-students/1").with(csrf()))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser
    void delete_notFound_returns404() throws Exception {
        org.mockito.Mockito.doThrow(new ResourceNotFoundException("ExamStudent", 99L))
                .when(examService).deleteById(99L);

        mockMvc.perform(delete("/api/v1/exam-students/99").with(csrf()))
                .andExpect(status().isNotFound());
    }
}
