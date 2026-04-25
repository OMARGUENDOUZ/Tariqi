package com.example.carly.service;

import com.example.carly.dto.StudentFilterDto;
import com.example.carly.exception.ResourceNotFoundException;
import com.example.carly.model.Student;
import com.example.carly.model.StudentStatus;
import com.example.carly.repository.ExamRepository;
import com.example.carly.repository.StudentRepository;
import com.example.carly.specification.StudentSpecifications;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.jpa.domain.Specification;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StudentServiceTest {

    @Mock
    private StudentRepository studentRepository;
    @Mock
    private ExamRepository examRepository;

    @InjectMocks
    private StudentService studentService;

    @Test
    void findById_found() {
        Student s = new Student();
        s.setId(1L);
        when(studentRepository.findById(1L)).thenReturn(Optional.of(s));

        Optional<Student> result = studentService.findById(1L);
        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(1L);
    }

    @Test
    void findById_notFound() {
        when(studentRepository.findById(99L)).thenReturn(Optional.empty());

        Optional<Student> result = studentService.findById(99L);
        assertThat(result).isEmpty();
    }

    @Test
    void create_persistsStudent() {
        Student s = new Student();
        s.setStatus(StudentStatus.REGISTERED);
        when(studentRepository.save(s)).thenReturn(s);

        Student result = studentService.create(s);
        assertThat(result).isEqualTo(s);
        verify(studentRepository).save(s);
    }

    @Test
    void update_notFound_throwsResourceNotFoundException() {
        when(studentRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> studentService.update(99L, new Student()))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Student");
    }

    @Test
    void deleteById_notFound_throwsResourceNotFoundException() {
        when(studentRepository.existsById(99L)).thenReturn(false);

        assertThatThrownBy(() -> studentService.deleteById(99L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void findWithFilters_returnsEmptyList() {
        when(studentRepository.findAll(any(Specification.class))).thenReturn(Collections.emptyList());

        List<Student> result = studentService.findWithFilters(new StudentFilterDto());
        assertThat(result).isEmpty();
    }
}
