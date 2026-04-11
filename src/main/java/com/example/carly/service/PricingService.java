package com.example.carly.service;

import com.example.carly.model.Pricing;
import com.example.carly.repository.PricingRepository;
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

    public Optional<Pricing> findById(long id) {
        return pricingRepository.findById(id);
    }

    public Pricing save(Pricing pricing) {
        return pricingRepository.save(pricing);
    }

    public Optional<Pricing> update(long id, Pricing pricing) {
        if (!pricingRepository.existsById(id)) {
            return Optional.empty();
        }
        pricing.setId(id);
        return Optional.of(pricingRepository.save(pricing));
    }

    public boolean deleteById(long id) {
        if (!pricingRepository.existsById(id)) {
            return false;
        }
        pricingRepository.deleteById(id);
        return true;
    }
}