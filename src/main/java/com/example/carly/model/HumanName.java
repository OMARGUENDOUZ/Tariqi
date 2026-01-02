package com.example.carly.model;

import jakarta.persistence.Embeddable;
import lombok.Data;

@Embeddable
@Data
public class HumanName {
    private String firstName;
    private String lastName;
}
