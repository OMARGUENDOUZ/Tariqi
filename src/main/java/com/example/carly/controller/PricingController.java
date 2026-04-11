package com.example.carly.controller;

import com.example.carly.dto.pricing.PricingRequest;
import com.example.carly.dto.pricing.PricingResponse;
import com.example.carly.mapper.PricingMapper;
import com.example.carly.model.Pricing;
import com.example.carly.repository.PricingRepository;
import com.example.carly.service.PricingService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/pricing")
public class PricingController {

    private final PricingService pricingService;
    private final PricingMapper pricingMapper;

    public PricingController(PricingService pricingService, PricingMapper pricingMapper) {
        this.pricingService = pricingService;
        this.pricingMapper = pricingMapper;
    }

    @GetMapping("/{id}")
    public ResponseEntity<PricingResponse> findById(@PathVariable long id) {
        return pricingService.findById(id)
                .map(pricingMapper::toResponse)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<List<PricingResponse>> findAll() {
        List<PricingResponse> pricings = pricingService.findAll()
                .stream()
                .map(pricingMapper::toResponse)
                .toList();
        return ResponseEntity.ok(pricings);
    }

    @PostMapping
    public ResponseEntity<PricingResponse> save(
            @RequestBody @Valid PricingRequest body,
            UriComponentsBuilder uriComponentsBuilder) {
        Pricing pricing = pricingService.save(pricingMapper.toEntity(body));
        URI location = uriComponentsBuilder
                .path("/api/v1/pricing/{id}")
                .buildAndExpand(pricing.getId())
                .toUri();
        return ResponseEntity.created(location).body(pricingMapper.toResponse(pricing));
    }

    @PutMapping("/{id}")
    public ResponseEntity<PricingResponse> update(
            @PathVariable long id,
            @RequestBody @Valid PricingRequest body) {
        Optional<Pricing> existing = pricingService.findById(id);
        if (existing.isPresent()) {
            Pricing toUpdate = pricingMapper.toEntity(body);
            toUpdate.setId(id);
            return ResponseEntity.ok(pricingMapper.toResponse(pricingService.save(toUpdate)));
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable long id) {
        if (pricingService.findById(id).isPresent()) {
            pricingService.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }
}