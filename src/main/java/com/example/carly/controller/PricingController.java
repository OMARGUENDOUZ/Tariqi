package com.example.carly.controller;

import com.example.carly.dto.pricing.PricingRequest;
import com.example.carly.dto.pricing.PricingResponse;
import com.example.carly.mapper.PricingMapper;
import com.example.carly.model.Pricing;
import com.example.carly.service.PricingService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;

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
        return ResponseEntity.ok(
                pricingService.findAll().stream().map(pricingMapper::toResponse).toList()
        );
    }

    @GetMapping("/page")
    public ResponseEntity<Page<PricingResponse>> findAllPaged(
            @PageableDefault(size = 20, sort = "id", direction = Sort.Direction.ASC) Pageable pageable) {
        return ResponseEntity.ok(pricingService.findAll(pageable).map(pricingMapper::toResponse));
    }

    @PostMapping
    public ResponseEntity<PricingResponse> save(
            @RequestBody @Valid PricingRequest body,
            UriComponentsBuilder uriComponentsBuilder) {
        Pricing pricing = pricingService.create(pricingMapper.toEntity(body));
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
        Pricing updated = pricingService.update(id, pricingMapper.toEntity(body));
        return ResponseEntity.ok(pricingMapper.toResponse(updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable long id) {
        pricingService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
