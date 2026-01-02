package com.example.carly.repository;

import com.example.carly.model.LicenseCategory;
import com.example.carly.model.Pricing;
import lombok.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PricingRepository extends JpaRepository<@NonNull Pricing, @NonNull Long> {

    Optional<Pricing> findByLicenseCategoryAndActiveTrue(LicenseCategory licenseCategory);
}
