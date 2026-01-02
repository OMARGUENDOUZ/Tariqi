package com.example.carly.config;

import com.example.carly.model.LicenseCategory;
import com.example.carly.model.Pricing;
import com.example.carly.repository.PricingRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner initPricing(PricingRepository pricingRepository) {
        return args -> {
            // Check if pricing exists for Category B, if not create it
            if (pricingRepository.findByLicenseCategoryAndActiveTrue(LicenseCategory.B).isEmpty()) {
                Pricing pricingB = new Pricing();
                pricingB.setLicenseCategory(LicenseCategory.B);
                pricingB.setBaseCourseFee(new BigDecimal("30000"));
                pricingB.setExamUnitFee(new BigDecimal("2000"));
                pricingB.setStampUnitFee(new BigDecimal("300"));
                pricingB.setMaxVehicles(2);
                pricingB.setCandidatesPerVehicle(20);
                pricingB.setActive(true);
                pricingRepository.save(pricingB);
                System.out.println("Initialized default pricing for Category B");
            }
        };
    }
}
