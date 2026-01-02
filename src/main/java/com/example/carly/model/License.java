package com.example.carly.model;

import jakarta.persistence.Embeddable;
import lombok.Data;

import java.util.Date;

@Embeddable
@Data
public class License {
    private Date obtentionDate;
    private long licenseNumber;
    private LicenseCategory licenseCategory;
    private Date issueDate;
    private String issuingAuthority;
    private Date expirationDate;
}
