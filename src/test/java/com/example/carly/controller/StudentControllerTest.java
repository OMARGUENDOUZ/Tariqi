package com.example.carly.controller;

import com.example.carly.dto.student.StudentRequest;
import com.example.carly.dto.student.StudentResponse;
import com.example.carly.exception.ResourceNotFoundException;
import com.example.carly.mapper.StudentMapper;
import com.example.carly.model.*;
import com.example.carly.security.JwtAuthFilter;
import com.example.carly.service.StudentService;
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
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = StudentController.class,
        excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = JwtAuthFilter.class))
class StudentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private StudentService studentService;

    @MockBean
    private StudentMapper studentMapper;

    private StudentResponse buildResponse(long id) {
        return new StudentResponse(id, null, "S001", "I001", StudentStatus.REGISTERED,
                "Ali", "Benali", new Date(), "Alger", "Rue 1", "0600000000",
                LicenseCategory.B, Collections.emptyList(), ExamCategory.CODE);
    }

    @Test
    @WithMockUser
    void findById_found_returns200() throws Exception {
        Student s = new Student();
        s.setId(1L);
        when(studentService.findById(1L)).thenReturn(Optional.of(s));
        when(studentMapper.toResponse(s)).thenReturn(buildResponse(1L));

        mockMvc.perform(get("/api/v1/students/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    @WithMockUser
    void findById_notFound_returns404() throws Exception {
        when(studentService.findById(99L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/v1/students/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser
    void searchStudents_emptyList_returns200WithEmptyArray() throws Exception {
        when(studentService.findWithFilters(any())).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/v1/students"))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
    }

    @Test
    @WithMockUser
    void save_validBody_returns201() throws Exception {
        StudentRequest req = new StudentRequest(null, "S001", "I001", StudentStatus.REGISTERED,
                "Ali", "Benali", new Date(), "Alger", "Rue 1", "0600000000",
                LicenseCategory.B, Collections.emptyList());
        Student s = new Student();
        s.setId(1L);

        when(studentMapper.toEntity(any())).thenReturn(s);
        when(studentService.create(s)).thenReturn(s);
        when(studentMapper.toResponse(s)).thenReturn(buildResponse(1L));

        mockMvc.perform(post("/api/v1/students")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    @WithMockUser
    void delete_notFound_returns404() throws Exception {
        when(studentService.existsById(99L)).thenReturn(false);
        // deleteById throws ResourceNotFoundException, handled by GlobalExceptionHandler -> 404
        org.mockito.Mockito.doThrow(new ResourceNotFoundException("Student", 99L))
                .when(studentService).deleteById(99L);

        mockMvc.perform(delete("/api/v1/students/99").with(csrf()))
                .andExpect(status().isNotFound());
    }
}
