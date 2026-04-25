package com.example.carly.service;

import com.example.carly.dto.StudentFilterDto;
import com.example.carly.exception.ResourceNotFoundException;
import com.example.carly.model.*;
import com.example.carly.repository.ExamRepository;
import com.example.carly.repository.StudentRepository;
import com.example.carly.specification.StudentSpecifications;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class StudentService {

    private final ExamRepository examRepository;
    private final StudentRepository studentRepository;

    public StudentService(StudentRepository studentRepository,
                          ExamRepository examRepository) {
        this.studentRepository = studentRepository;
        this.examRepository = examRepository;
    }

    public List<Student> findWithFilters(StudentFilterDto filters) {
        List<Student> students = studentRepository.findAll(
                Specification.where(StudentSpecifications.hasFirstName(filters.getFirstName()))
                        .and(StudentSpecifications.hasLastName(filters.getLastName()))
                        .and(StudentSpecifications.hasFatherFirstName(filters.getFatherFirstName()))
                        .and(StudentSpecifications.hasFatherLastName(filters.getFatherLastName()))
                        .and(StudentSpecifications.hasMotherFirstName(filters.getMotherFirstName()))
                        .and(StudentSpecifications.hasMotherLastName(filters.getMotherLastName()))
                        .and(StudentSpecifications.hasPhoneNumber(filters.getPhoneNumber()))
                        .and(StudentSpecifications.hasGender(filters.getGender()))
                        .and(StudentSpecifications.hasRequestedLicense(filters.getRequestedLicense()))
                        .and(StudentSpecifications.hasStatus(filters.getStatus()))
                        .and(StudentSpecifications.hasExamStatus(filters.getExamStatus()))
                        .and(StudentSpecifications.hasExamOnDate(filters.getExamDate()))
                        .and(StudentSpecifications.hasExamCategory(filters.getExamCategory()))
                        .and(StudentSpecifications.hasExamResult(filters.getExamResult())));

        populateTransientFields(students);
        return students;
    }

    public Page<Student> findAll(Pageable pageable) {
        Page<Student> page = studentRepository.findAll(pageable);
        populateTransientFields(page.getContent());
        return page;
    }

    private void populateTransientFields(List<Student> students) {
        for (Student student : students) {
            List<ExamStudent> exams = examRepository.findByStudentId(student.getId());

            if (!exams.isEmpty()) {
                exams.stream()
                        .filter(e -> e.getResult() != ExamResult.ABSENT_JUSTIFIED
                                && e.getResult() != ExamResult.ABSENT_UNJUSTIFIED
                                && e.getResult() != ExamResult.PENDING)
                        .sorted((e1, e2) -> e2.getDate().compareTo(e1.getDate()))
                        .findFirst()
                        .ifPresent(student::setLastExam);
            }

            boolean hasCode = exams.stream().anyMatch(e -> e.getCategory() == ExamCategory.CODE
                    && e.getResult() == ExamResult.PASS);
            boolean hasCreneau = exams.stream()
                    .anyMatch(e -> e.getCategory() == ExamCategory.CRENEAU
                            && e.getResult() == ExamResult.PASS);
            boolean hasConduite = exams.stream()
                    .anyMatch(e -> e.getCategory() == ExamCategory.CONDUITE
                            && e.getResult() == ExamResult.PASS);

            if (!hasCode) {
                student.setNextExam(ExamCategory.CODE);
            } else if (!hasCreneau) {
                student.setNextExam(ExamCategory.CRENEAU);
            } else if (!hasConduite) {
                student.setNextExam(ExamCategory.CONDUITE);
            } else {
                student.setNextExam(null);
            }
        }
    }

    public Student uploadPhotoBase64(Long id, String payload) {
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Student", id));
        student.setPhotoBase64(payload);
        return studentRepository.save(student);
    }

    public Optional<Student> findById(Long id) {
        return studentRepository.findById(id);
    }

    public Student create(Student student) {
        return studentRepository.save(student);
    }

    public Student update(Long id, Student patch) {
        Student existing = studentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Student", id));
        existing.setName(patch.getName());
        existing.setPhotoBase64(patch.getPhotoBase64());
        existing.setSchoolId(patch.getSchoolId());
        existing.setInscriptionId(patch.getInscriptionId());
        existing.setStatus(patch.getStatus());
        existing.setBirthDate(patch.getBirthDate());
        existing.setPlaceOfBirth(patch.getPlaceOfBirth());
        existing.setFatherName(patch.getFatherName());
        existing.setMotherName(patch.getMotherName());
        existing.setAddress(patch.getAddress());
        existing.setPhoneNumber(patch.getPhoneNumber());
        existing.setInscriptionDate(patch.getInscriptionDate());
        existing.setInscriptionSchoolDate(patch.getInscriptionSchoolDate());
        existing.setRequestedLicense(patch.getRequestedLicense());
        existing.setOwnedLicense(patch.getOwnedLicense());
        return studentRepository.save(existing);
    }

    public void deleteById(Long id) {
        if (!studentRepository.existsById(id)) {
            throw new ResourceNotFoundException("Student", id);
        }
        studentRepository.deleteById(id);
    }

    public boolean existsById(Long id) {
        return studentRepository.existsById(id);
    }
}
