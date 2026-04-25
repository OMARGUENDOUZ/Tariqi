package com.example.carly.service;

import com.example.carly.dto.examstudent.ExamStudentFilter;
import com.example.carly.exception.ExamRegistrationException;
import com.example.carly.exception.ResourceNotFoundException;
import com.example.carly.model.*;
import com.example.carly.repository.ExamRepository;
import com.example.carly.repository.ExamSlotRepository;
import com.example.carly.repository.PricingRepository;
import com.example.carly.repository.StudentRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class ExamService {

    private final ExamRepository examRepository;
    private final ExamSlotRepository examSlotRepository;
    private final StudentRepository studentRepository;
    private final PricingRepository pricingRepository;

    public ExamService(ExamRepository examRepository, ExamSlotRepository examSlotRepository,
                       StudentRepository studentRepository, PricingRepository pricingRepository) {
        this.examRepository = examRepository;
        this.examSlotRepository = examSlotRepository;
        this.studentRepository = studentRepository;
        this.pricingRepository = pricingRepository;
    }

    public Optional<ExamStudent> findById(long id) {
        return examRepository.findById(id);
    }

    public List<ExamStudent> findWithFilter(ExamStudentFilter filter) {
        if (filter.examSlotId() != null) {
            return examRepository.findByExamSlotId(filter.examSlotId());
        }
        if (filter.studentId() != null && filter.status() != null) {
            return examRepository.findByStudentIdAndStatus(filter.studentId(), filter.status());
        }
        if (filter.studentId() != null) {
            return examRepository.findByStudentId(filter.studentId());
        }
        if (filter.status() != null) {
            return examRepository.findByStatus(filter.status());
        }
        return examRepository.findAll();
    }

    public Page<ExamStudent> findAll(Pageable pageable) {
        return examRepository.findAll(pageable);
    }

    @Transactional
    public ExamStudent update(long id, ExamStudent patch) {
        ExamStudent existing = examRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("ExamStudent", id));
        if (patch.getResult() != null) {
            existing.setResult(patch.getResult());
        }
        if (patch.getStatus() != null) {
            existing.setStatus(patch.getStatus());
        }
        if (patch.getCategory() != null) {
            existing.setCategory(patch.getCategory());
        }
        return examRepository.save(existing);
    }

    public void deleteById(long id) {
        if (!examRepository.existsById(id)) {
            throw new ResourceNotFoundException("ExamStudent", id);
        }
        examRepository.deleteById(id);
    }

    public boolean existsById(long id) {
        return examRepository.existsById(id);
    }

    @Transactional
    public ExamStudent registerStudentForExam(Long studentId, Long examSlotId, ExamCategory category) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student", studentId));
        ExamSlot slot = examSlotRepository.findById(examSlotId)
                .orElseThrow(() -> new ResourceNotFoundException("ExamSlot", examSlotId));

        checkEligibility(student, category);

        Pricing pricing = pricingRepository.findByLicenseCategoryAndActiveTrue(student.getRequestedLicense())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No active pricing found for category: " + student.getRequestedLicense()));

        long currentCount = examRepository.countByExamSlotIdAndCategory(examSlotId, category);
        int maxCapacity = pricing.getMaxVehicles() * pricing.getCandidatesPerVehicle();

        if (currentCount >= maxCapacity) {
            throw new ExamRegistrationException("Exam slot is full for category " + category);
        }

        ExamStudent registration = new ExamStudent();
        registration.setStudentId(student.getId());
        registration.setExamSlotId(slot.getId());
        registration.setCategory(category);
        registration.setStatus(ExamStatus.PLANNED);
        registration.setResult(ExamResult.PENDING);
        registration.setDate(java.sql.Date.valueOf(slot.getExamDate()));

        boolean hasExamOnDate = examRepository.findByStudentId(registration.getStudentId()).stream()
                .anyMatch(e -> e.getDate().equals(registration.getDate())
                        && e.getResult() != ExamResult.FAIL);

        if (hasExamOnDate) {
            throw new ExamRegistrationException("Student already has an exam scheduled on this date");
        }

        return examRepository.save(registration);
    }

    private void checkEligibility(Student student, ExamCategory category) {
        if (category == ExamCategory.CODE) {
            boolean alreadyPassed = examRepository.existsByStudentIdAndCategoryAndResult(student.getId(),
                    ExamCategory.CODE, ExamResult.PASS);
            if (alreadyPassed) {
                throw new ExamRegistrationException("Student has already passed CODE exam.");
            }
        } else if (category == ExamCategory.CRENEAU) {
            boolean codePassed = examRepository.existsByStudentIdAndCategoryAndResult(student.getId(),
                    ExamCategory.CODE, ExamResult.PASS);
            if (!codePassed) {
                throw new ExamRegistrationException("Prerequisite failed: Must pass CODE before CRENEAU.");
            }
        } else if (category == ExamCategory.CONDUITE) {
            boolean creneauPassed = examRepository.existsByStudentIdAndCategoryAndResult(student.getId(),
                    ExamCategory.CRENEAU, ExamResult.PASS);
            if (!creneauPassed) {
                throw new ExamRegistrationException("Prerequisite failed: Must pass CRENEAU before CONDUITE.");
            }
        }
    }
}
