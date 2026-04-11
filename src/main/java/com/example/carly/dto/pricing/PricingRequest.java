package com.example.carly.dto.pricing;

import com.example.carly.model.LicenseCategory;
import java.math.BigDecimal;

public record PricingRequest(
        LicenseCategory licenseCategory,
        BigDecimal baseCourseFee,
        BigDecimal examUnitFee,
        BigDecimal stampUnitFee,
        boolean active,
        int maxVehicles,
        int candidatesPerVehicle,
        boolean billExamOnJustifiedAbsence,
        boolean billStampOnJustifiedAbsence,
        boolean billExamOnUnjustifiedAbsence,
        boolean billStampOnUnjustifiedAbsence
) {
}