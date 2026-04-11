package com.example.carly.mapper;

import com.example.carly.dto.pricing.PricingRequest;
import com.example.carly.dto.pricing.PricingResponse;
import com.example.carly.model.Pricing;
import org.springframework.stereotype.Component;

@Component
public class PricingMapper {

    public PricingResponse toResponse(Pricing pricing) {
        return new PricingResponse(
                pricing.getId(),
                pricing.getLicenseCategory(),
                pricing.getBaseCourseFee(),
                pricing.getExamUnitFee(),
                pricing.getStampUnitFee(),
                pricing.isActive(),
                pricing.getMaxVehicles(),
                pricing.getCandidatesPerVehicle(),
                pricing.isBillExamOnJustifiedAbsence(),
                pricing.isBillStampOnJustifiedAbsence(),
                pricing.isBillExamOnUnjustifiedAbsence(),
                pricing.isBillStampOnUnjustifiedAbsence()
        );
    }

    public Pricing toEntity(PricingRequest request) {
        Pricing pricing = new Pricing();
        pricing.setLicenseCategory(request.licenseCategory());
        pricing.setBaseCourseFee(request.baseCourseFee());
        pricing.setExamUnitFee(request.examUnitFee());
        pricing.setStampUnitFee(request.stampUnitFee());
        pricing.setActive(request.active());
        pricing.setMaxVehicles(request.maxVehicles());
        pricing.setCandidatesPerVehicle(request.candidatesPerVehicle());
        pricing.setBillExamOnJustifiedAbsence(request.billExamOnJustifiedAbsence());
        pricing.setBillStampOnJustifiedAbsence(request.billStampOnJustifiedAbsence());
        pricing.setBillExamOnUnjustifiedAbsence(request.billExamOnUnjustifiedAbsence());
        pricing.setBillStampOnUnjustifiedAbsence(request.billStampOnUnjustifiedAbsence());
        return pricing;
    }
}