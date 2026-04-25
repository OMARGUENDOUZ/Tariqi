package com.example.carly.service;

import com.example.carly.exception.ResourceNotFoundException;
import com.example.carly.model.Pricing;
import com.example.carly.repository.PricingRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PricingService {

    private final PricingRepository pricingRepository;

    public PricingService(PricingRepository pricingRepository) {
        this.pricingRepository = pricingRepository;
    }

    public List<Pricing> findAll() {
        return pricingRepository.findAll();
    }

    public Page<Pricing> findAll(Pageable pageable) {
        return pricingRepository.findAll(pageable);
    }

    public Optional<Pricing> findById(long id) {
        return pricingRepository.findById(id);
    }

    public Pricing create(Pricing pricing) {
        return pricingRepository.save(pricing);
    }

    public Pricing update(long id, Pricing patch) {
        Pricing existing = pricingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pricing", id));
        existing.setLicenseCategory(patch.getLicenseCategory());
        existing.setBaseCourseFee(patch.getBaseCourseFee());
        existing.setExamUnitFee(patch.getExamUnitFee());
        existing.setStampUnitFee(patch.getStampUnitFee());
        existing.setActive(patch.isActive());
        existing.setMaxVehicles(patch.getMaxVehicles());
        existing.setCandidatesPerVehicle(patch.getCandidatesPerVehicle());
        existing.setBillExamOnJustifiedAbsence(patch.isBillExamOnJustifiedAbsence());
        existing.setBillStampOnJustifiedAbsence(patch.isBillStampOnJustifiedAbsence());
        existing.setBillExamOnUnjustifiedAbsence(patch.isBillExamOnUnjustifiedAbsence());
        existing.setBillStampOnUnjustifiedAbsence(patch.isBillStampOnUnjustifiedAbsence());
        return pricingRepository.save(existing);
    }

    public void deleteById(long id) {
        if (!pricingRepository.existsById(id)) {
            throw new ResourceNotFoundException("Pricing", id);
        }
        pricingRepository.deleteById(id);
    }
}
