package com.example.carly.mapper;

import com.example.carly.dto.instructor.InstructorRequest;
import com.example.carly.dto.instructor.InstructorResponse;
import com.example.carly.model.HumanName;
import com.example.carly.model.Instructor;
import org.springframework.stereotype.Component;

@Component
public class InstructorMapper {

    public InstructorResponse toResponse(Instructor instructor) {
        return new InstructorResponse(
                instructor.getId(),
                instructor.getName() != null ? instructor.getName().getFirstName() : null,
                instructor.getName() != null ? instructor.getName().getLastName() : null,
                instructor.getBirthDate(),
                instructor.getPlaceOfBirth(),
                instructor.getAddress(),
                instructor.getPhoneNumber()
        );
    }

    public Instructor toEntity(InstructorRequest request) {
        Instructor instructor = new Instructor();

        HumanName name = new HumanName();
        name.setFirstName(request.firstName());
        name.setLastName(request.lastName());

        instructor.setName(name);
        instructor.setBirthDate(request.birthDate());
        instructor.setPlaceOfBirth(request.placeOfBirth());
        instructor.setAddress(request.address());
        instructor.setPhoneNumber(request.phoneNumber());

        return instructor;
    }
}
