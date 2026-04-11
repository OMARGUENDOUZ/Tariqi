package com.example.carly.mapper;

import com.example.carly.dto.student.StudentRequest;
import com.example.carly.dto.student.StudentResponse;
import com.example.carly.model.HumanName;
import com.example.carly.model.Student;
import org.springframework.stereotype.Component;

@Component
public class StudentMapper {

    public StudentResponse toResponse(Student student) {
        return new StudentResponse(
                student.getId(),
                student.getPhotoBase64(),
                student.getSchoolId(),
                student.getInscriptionId(),
                student.getStatus(),
                student.getName() != null ? student.getName().getFirstName() : null,
                student.getName() != null ? student.getName().getLastName() : null,
                student.getBirthDate(),
                student.getPlaceOfBirth(),
                student.getAddress(),
                student.getPhoneNumber(),
                student.getRequestedLicense(),
                student.getOwnedLicense(),
                student.getNextExam()
        );
    }

    public Student toEntity(StudentRequest request) {
        Student student = new Student();

        HumanName name = new HumanName();
        name.setFirstName(request.firstName());
        name.setLastName(request.lastName());

        student.setName(name);
        student.setPhotoBase64(request.photoBase64());
        student.setSchoolId(request.schoolId());
        student.setInscriptionId(request.inscriptionId());
        student.setStatus(request.status());
        student.setBirthDate(request.birthDate());
        student.setPlaceOfBirth(request.placeOfBirth());
        student.setAddress(request.address());
        student.setPhoneNumber(request.phoneNumber());
        student.setRequestedLicense(request.requestedLicense());
        student.setOwnedLicense(request.ownedLicense());

        return student;
    }
}
