package com.example.carly.repository;

import com.example.carly.model.ExamCategory;
import com.example.carly.model.ExamStudent;
import com.example.carly.model.ExamResult;
import com.example.carly.model.ExamStatus;
import lombok.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExamRepository extends JpaRepository<@NonNull ExamStudent, @NonNull Long> {

    List<ExamStudent> findByStudentId(Long studentId);

    List<ExamStudent> findByStatus(ExamStatus status);

    List<ExamStudent> findByStudentIdAndStatus(Long studentId, ExamStatus status);

    List<ExamStudent> findByResult(ExamResult result);

    long countByExamSlotIdAndCategory(Long examSlotId, ExamCategory category);

    List<ExamStudent> findByExamSlotId(Long examSlotId);

    boolean existsByStudentIdAndCategoryAndResult(Long studentId, ExamCategory category, ExamResult result);
}
