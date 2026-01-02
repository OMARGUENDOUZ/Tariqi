package com.example.carly.model;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;

@Entity
@Data
public class Pricing {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Enumerated
    private LicenseCategory licenseCategory;
    private BigDecimal baseCourseFee;
    private BigDecimal examUnitFee;
    private BigDecimal stampUnitFee;
    private boolean active = true;

    @Column(nullable = false)
    private int maxVehicles = 2;

    @Column(nullable = false)
    private int candidatesPerVehicle = 20;

    // Billing Rules
    private boolean billExamOnJustifiedAbsence = false;
    private boolean billStampOnJustifiedAbsence = false;
    private boolean billExamOnUnjustifiedAbsence = false; // Usually true or false depending on school
    private boolean billStampOnUnjustifiedAbsence = true; // Usually true
}
