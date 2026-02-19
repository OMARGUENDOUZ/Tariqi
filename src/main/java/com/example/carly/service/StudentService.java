package com.example.carly.service;

import com.example.carly.dto.StudentFilterDto;
import com.example.carly.model.Student;
import com.example.carly.repository.StudentRepository;
import com.example.carly.specification.StudentSpecifications;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class StudentService {

    private final com.example.carly.repository.ExamRepository examRepository;
    private final StudentRepository studentRepository;

    public StudentService(StudentRepository studentRepository,
            com.example.carly.repository.ExamRepository examRepository) {
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

        // Populate transient fields
        for (Student student : students) {
            List<com.example.carly.model.ExamStudent> exams = examRepository.findByStudentId(student.getId());

            // Determine last exam (excluding absences)
            if (!exams.isEmpty()) {
                exams.stream()
                        .filter(e -> e.getResult() != com.example.carly.model.ExamResult.ABSENT_JUSTIFIED
                                && e.getResult() != com.example.carly.model.ExamResult.ABSENT_UNJUSTIFIED
                                && e.getResult() != com.example.carly.model.ExamResult.PENDING)
                        .sorted((e1, e2) -> e2.getDate().compareTo(e1.getDate()))
                        .findFirst()
                        .ifPresent(student::setLastExam);
            }

            // Determine next exam
            boolean hasCode = exams.stream().anyMatch(e -> e.getCategory() == com.example.carly.model.ExamCategory.CODE
                    && e.getResult() == com.example.carly.model.ExamResult.PASS);
            boolean hasCreneau = exams.stream()
                    .anyMatch(e -> e.getCategory() == com.example.carly.model.ExamCategory.CRENEAU
                            && e.getResult() == com.example.carly.model.ExamResult.PASS);
            boolean hasConduite = exams.stream()
                    .anyMatch(e -> e.getCategory() == com.example.carly.model.ExamCategory.CONDUITE
                            && e.getResult() == com.example.carly.model.ExamResult.PASS);

            if (!hasCode) {
                student.setNextExam(com.example.carly.model.ExamCategory.CODE);
            } else if (!hasCreneau) {
                student.setNextExam(com.example.carly.model.ExamCategory.CRENEAU);
            } else if (!hasConduite) {
                student.setNextExam(com.example.carly.model.ExamCategory.CONDUITE);
            } else {
                student.setNextExam(null); // All passed
            }
        }

        return students;
    }

    public Student uploadPhotoBase64(Long id, Map<String, String> payload) {
        Optional<Student> studentOp = studentRepository.findById(id);

        if (studentOp.isPresent()) {
            Student student = studentOp.get();
            student.setPhotoBase64(payload.get("photoBase64"));
            studentRepository.save(student);
            return student;
        }
        return null;
    }

    public Optional<Student> findById(Long id) {
        return studentRepository.findById(id);
    }

    public Student save(Student student) {
        return studentRepository.save(student);
    }

    public Student save(Long id,Student student) {
        Optional<Student> studentOpt =  findById(id);
        return (studentOpt.isPresent()) ? studentRepository.save(student): null;
    }

    public void deleteById(Long id) {
        studentRepository.deleteById(id);
    }

    public boolean existsById(Long id) {
        return studentRepository.existsById(id);
    }
}
