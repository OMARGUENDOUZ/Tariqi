package com.example.carly.service;

import com.example.carly.model.*;
import com.example.carly.repository.ExamRepository;
import com.example.carly.repository.ExamSlotRepository;
import com.example.carly.repository.PricingRepository;
import com.example.carly.repository.StudentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    @Transactional
    public ExamStudent registerStudentForExam(Long studentId, Long examSlotId, ExamCategory category) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found"));
        ExamSlot slot = examSlotRepository.findById(examSlotId)
                .orElseThrow(() -> new RuntimeException("Exam slot not found"));

        // 1. Check Eligibility
        checkEligibility(student, category);

        // 2. Check Capacity & Assign Vehicle
        Pricing pricing = pricingRepository.findByLicenseCategoryAndActiveTrue(student.getRequestedLicense())
                .orElseThrow(() -> new RuntimeException(
                        "No active pricing found for category: " + student.getRequestedLicense()));

        long currentCount = examRepository.countByExamSlotIdAndCategory(examSlotId, category);
        int maxCapacity = pricing.getMaxVehicles() * pricing.getCandidatesPerVehicle();

        if (currentCount >= maxCapacity) {
            throw new RuntimeException("Exam slot is full for category " + category);
        }

        // Determine vehicle assignment (logic can be refined, currently just validating
        // capacity)
        // Vehicle logic is implicit: indices 0 to N-1 are Vehicle 1, N to 2N-1 are
        // Vehicle 2.

        // 3. Register
        ExamStudent registration = new ExamStudent();
        registration.setStudentId(student.getId());
        registration.setExamSlotId(slot.getId());
        registration.setCategory(category);
        registration.setStatus(ExamStatus.PLANNED);
        registration.setResult(ExamResult.PENDING);
        registration.setDate(java.sql.Date.valueOf(slot.getExamDate())); // Copy date from slot

        // Check if student already has an exam on this date
        boolean hasExamOnDate = examRepository.findByStudentId(registration.getStudentId()).stream()
                .anyMatch(e -> e.getDate().equals(registration.getDate())
                        && e.getResult() != com.example.carly.model.ExamResult.FAIL);

        if (hasExamOnDate) {
            throw new RuntimeException("Student already has an exam scheduled on this date");
        }

        return examRepository.save(registration);
    }

    private void checkEligibility(Student student, ExamCategory category) {
        if (category == ExamCategory.CODE) {
            // Can register if not already passed?
            // Assuming multiple tries are allowed, but shouldn't be currently passed.
            boolean alreadyPassed = examRepository.existsByStudentIdAndCategoryAndResult(student.getId(),
                    ExamCategory.CODE, ExamResult.PASS);
            if (alreadyPassed) {
                throw new RuntimeException("Student has already passed CODE exam.");
            }
        } else if (category == ExamCategory.CRENEAU) {
            boolean codePassed = examRepository.existsByStudentIdAndCategoryAndResult(student.getId(),
                    ExamCategory.CODE, ExamResult.PASS);
            if (!codePassed) {
                throw new RuntimeException("Prerequisite failed: Must pass CODE before CRENEAU.");
            }
        } else if (category == ExamCategory.CONDUITE) {
            boolean creneauPassed = examRepository.existsByStudentIdAndCategoryAndResult(student.getId(),
                    ExamCategory.CRENEAU, ExamResult.PASS);
            if (!creneauPassed) {
                throw new RuntimeException("Prerequisite failed: Must pass CRENEAU before CONDUITE.");
            }
        }
    }
}
