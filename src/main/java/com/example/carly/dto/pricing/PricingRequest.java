package com.example.carly.dto.pricing;

import com.example.carly.model.LicenseCategory;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record PricingRequest(
        @NotNull LicenseCategory licenseCategory,
        @NotNull BigDecimal baseCourseFee,
        @NotNull BigDecimal examUnitFee,
        @NotNull BigDecimal stampUnitFee,
        @NotNull boolean active,
        @NotNull int maxVehicles,
        @NotNull int candidatesPerVehicle,
        @NotNull boolean billExamOnJustifiedAbsence,
        @NotNull boolean billStampOnJustifiedAbsence,
        @NotNull boolean billExamOnUnjustifiedAbsence,
        @NotNull boolean billStampOnUnjustifiedAbsence
) {
}