package com.example.carly.repository;

import com.example.carly.model.ExamStatus;
import com.example.carly.model.LicenseCategory;
import com.example.carly.model.PaymentStatus;
import com.example.carly.model.Student;
import lombok.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface StudentRepository extends JpaRepository<@NonNull Student, @NonNull Long>, JpaSpecificationExecutor<Student> {
    @Query("""
       SELECT DISTINCT s
       FROM Student s
       JOIN ExamStudent e ON e.studentId = s.id
       WHERE e.status = :status
       """)
    List<Student> findByExamStatus(ExamStatus status);
    @Query("""
       SELECT DISTINCT s
       FROM Student s
       JOIN ExamStudent e ON e.studentId = s.id
       WHERE e.date = :date
       """)
    List<Student> findByExamDate(Date date);
    @Query("""
       SELECT DISTINCT s
       FROM Student s
       JOIN Invoice i ON i.studentId = s.id
       WHERE i.status = :status
       """)
    List<Student> findByInvoiceStatus(PaymentStatus status);

    List<Student> findByRequestedLicense(LicenseCategory requestedLicense);
}
