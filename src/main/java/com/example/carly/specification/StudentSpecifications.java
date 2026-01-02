package com.example.carly.specification;

import com.example.carly.model.*;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;
import org.springframework.data.jpa.domain.Specification;

import java.util.Date;

public class StudentSpecifications {

    // Champs directs de Student
    public static Specification<Student> hasFirstName(String firstName) {
        return (root, query, cb) -> firstName == null ? null :
                cb.like(cb.lower(root.get("name").get("firstName")),
                        "%" + firstName.toLowerCase() + "%");
    }

    public static Specification<Student> hasLastName(String lastName) {
        return (root, query, cb) -> lastName == null ? null :
                cb.like(cb.lower(root.get("name").get("lastName")),
                        "%" + lastName.toLowerCase() + "%");
    }

    public static Specification<Student> hasFatherFirstName(String name) {
        return (root, query, cb) -> name == null ? null :
                cb.like(cb.lower(root.get("fatherName").get("firstName")),
                        "%" + name.toLowerCase() + "%");
    }

    public static Specification<Student> hasFatherLastName(String name) {
        return (root, query, cb) -> name == null ? null :
                cb.like(cb.lower(root.get("fatherName").get("lastName")),
                        "%" + name.toLowerCase() + "%");
    }

    public static Specification<Student> hasMotherFirstName(String name) {
        return (root, query, cb) -> name == null ? null :
                cb.like(cb.lower(root.get("motherName").get("firstName")),
                        "%" + name.toLowerCase() + "%");
    }

    public static Specification<Student> hasMotherLastName(String name) {
        return (root, query, cb) -> name == null ? null :
                cb.like(cb.lower(root.get("motherName").get("lastName")),
                        "%" + name.toLowerCase() + "%");
    }

    public static Specification<Student> hasPhoneNumber(String phone) {
        return (root, query, cb) -> phone == null ? null :
                cb.like(root.get("phoneNumber"), "%" + phone + "%");
    }

    public static Specification<Student> hasGender(GenderType gender) {
        return (root, query, cb) -> gender == null ? null :
                cb.equal(root.get("gender"), gender);
    }

    public static Specification<Student> hasRequestedLicense(LicenseCategory category) {
        return (root, query, cb) -> category == null ? null :
                cb.equal(root.get("requestedLicense"), category);
    }

    public static Specification<Student> hasStatus(StudentStatus status) {
        return (root, query, cb) -> status == null ? null :
                cb.equal(root.get("status"), status);
    }

    // Filtres sur Exam (avec subquery)
    public static Specification<Student> hasExamStatus(ExamStatus examStatus) {
        return (root, query, cb) -> {
            if (examStatus == null) return null;

            jakarta.persistence.criteria.Subquery<Long> subquery = query.subquery(Long.class);
            Root<ExamStudent> examRoot = subquery.from(ExamStudent.class);
            subquery.select(examRoot.get("studentId"))
                    .where(cb.equal(examRoot.get("status"), examStatus));

            return root.get("id").in(subquery);
        };
    }

    public static Specification<Student> hasExamOnDate(Date examDate) {
        return (root, query, cb) -> {
            if (examDate == null) return null;

            Subquery<Long> subquery = query.subquery(Long.class);
            Root<ExamStudent> examRoot = subquery.from(ExamStudent.class);
            subquery.select(examRoot.get("studentId"))
                    .where(cb.equal(examRoot.get("date"), examDate));

            return root.get("id").in(subquery);
        };
    }

    public static Specification<Student> hasExamCategory(ExamCategory category) {
        return (root, query, cb) -> {
            if (category == null) return null;

            Subquery<Long> subquery = query.subquery(Long.class);
            Root<ExamStudent> examRoot = subquery.from(ExamStudent.class);
            subquery.select(examRoot.get("studentId"))
                    .where(cb.equal(examRoot.get("category"), category));

            return root.get("id").in(subquery);
        };
    }

    public static Specification<Student> hasExamResult(ExamResult result) {
        return (root, query, cb) -> {
            if (result == null) return null;

            Subquery<Long> subquery = query.subquery(Long.class);
            Root<ExamStudent> examRoot = subquery.from(ExamStudent.class);
            subquery.select(examRoot.get("studentId"))
                    .where(cb.equal(examRoot.get("result"), result));

            return root.get("id").in(subquery);
        };
    }
}

