package com.example.carly.service;

import com.example.carly.exception.ExamRegistrationException;
import com.example.carly.exception.ResourceNotFoundException;
import com.example.carly.model.*;
import com.example.carly.repository.ExamRepository;
import com.example.carly.repository.ExamSlotRepository;
import com.example.carly.repository.PricingRepository;
import com.example.carly.repository.StudentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ExamServiceTest {

    @Mock
    private ExamRepository examRepository;
    @Mock
    private ExamSlotRepository examSlotRepository;
    @Mock
    private StudentRepository studentRepository;
    @Mock
    private PricingRepository pricingRepository;

    @InjectMocks
    private ExamService examService;

    private Student student;
    private ExamSlot slot;
    private Pricing pricing;

    @BeforeEach
    void setUp() {
        student = new Student();
        student.setId(1L);
        student.setRequestedLicense(LicenseCategory.B);

        slot = new ExamSlot();
        slot.setId(10L);
        slot.setExamDate(LocalDate.of(2025, 6, 15));

        pricing = new Pricing();
        pricing.setLicenseCategory(LicenseCategory.B);
        pricing.setMaxVehicles(2);
        pricing.setCandidatesPerVehicle(20);
        pricing.setActive(true);
    }

    @Test
    void registerStudentForExam_success() {
        when(studentRepository.findById(1L)).thenReturn(Optional.of(student));
        when(examSlotRepository.findById(10L)).thenReturn(Optional.of(slot));
        when(pricingRepository.findByLicenseCategoryAndActiveTrue(LicenseCategory.B)).thenReturn(Optional.of(pricing));
        when(examRepository.existsByStudentIdAndCategoryAndResult(1L, ExamCategory.CODE, ExamResult.PASS)).thenReturn(false);
        when(examRepository.countByExamSlotIdAndCategory(10L, ExamCategory.CODE)).thenReturn(0L);
        when(examRepository.findByStudentId(1L)).thenReturn(Collections.emptyList());
        when(examRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        ExamStudent result = examService.registerStudentForExam(1L, 10L, ExamCategory.CODE);

        assertThat(result.getStudentId()).isEqualTo(1L);
        assertThat(result.getExamSlotId()).isEqualTo(10L);
        assertThat(result.getCategory()).isEqualTo(ExamCategory.CODE);
        assertThat(result.getStatus()).isEqualTo(ExamStatus.PLANNED);
        verify(examRepository).save(any(ExamStudent.class));
    }

    @Test
    void registerStudentForExam_studentNotFound_throwsResourceNotFoundException() {
        when(studentRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> examService.registerStudentForExam(99L, 10L, ExamCategory.CODE))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Student");
    }

    @Test
    void registerStudentForExam_alreadyPassedCode_throwsExamRegistrationException() {
        when(studentRepository.findById(1L)).thenReturn(Optional.of(student));
        when(examSlotRepository.findById(10L)).thenReturn(Optional.of(slot));
        when(examRepository.existsByStudentIdAndCategoryAndResult(1L, ExamCategory.CODE, ExamResult.PASS)).thenReturn(true);

        assertThatThrownBy(() -> examService.registerStudentForExam(1L, 10L, ExamCategory.CODE))
                .isInstanceOf(ExamRegistrationException.class)
                .hasMessageContaining("already passed CODE");
    }

    @Test
    void registerStudentForExam_slotFull_throwsExamRegistrationException() {
        when(studentRepository.findById(1L)).thenReturn(Optional.of(student));
        when(examSlotRepository.findById(10L)).thenReturn(Optional.of(slot));
        when(pricingRepository.findByLicenseCategoryAndActiveTrue(LicenseCategory.B)).thenReturn(Optional.of(pricing));
        when(examRepository.existsByStudentIdAndCategoryAndResult(1L, ExamCategory.CODE, ExamResult.PASS)).thenReturn(false);
        when(examRepository.countByExamSlotIdAndCategory(10L, ExamCategory.CODE)).thenReturn(40L); // 2*20 = 40 max

        assertThatThrownBy(() -> examService.registerStudentForExam(1L, 10L, ExamCategory.CODE))
                .isInstanceOf(ExamRegistrationException.class)
                .hasMessageContaining("full");
    }

    @Test
    void deleteById_notFound_throwsResourceNotFoundException() {
        when(examRepository.existsById(999L)).thenReturn(false);

        assertThatThrownBy(() -> examService.deleteById(999L))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}
