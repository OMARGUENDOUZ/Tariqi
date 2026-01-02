package com.example.carly.controller;

import com.example.carly.model.Pricing;
import com.example.carly.repository.PricingRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.Optional;

@RestController
@RequestMapping("/Pricing")
public class PricingController {
    private final PricingRepository pricingRepository;

    public PricingController(PricingRepository pricingRepository) {
        this.pricingRepository = pricingRepository;
    }

    @GetMapping("/{id}")
    public ResponseEntity<Pricing> findById(@PathVariable long id) {
        Optional<Pricing> pricing = pricingRepository.findById(id);

        return pricing.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping
    public Iterable<Pricing> findAll() {
        Iterable<Pricing> pricings = pricingRepository.findAll();
        return ResponseEntity.ok(pricings).getBody();
    }

    @PostMapping
    public Pricing save(@RequestBody Pricing body, UriComponentsBuilder uriComponentsBuilder) {
        Pricing pricing = pricingRepository.save(body);
        URI location = uriComponentsBuilder.path("/Pricing/{id}").buildAndExpand(pricing.getId()).toUri();
        return ResponseEntity.created(location).body(pricing).getBody();
    }

    @PutMapping("{id}")
    public ResponseEntity<Pricing> update(@PathVariable long id, @RequestBody Pricing body) {
        Optional<Pricing> pricing = pricingRepository.findById(id);

        if(pricing.isPresent()) {
            Pricing updated = pricingRepository.save(body);
            return ResponseEntity.ok(updated);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Pricing> delete(@PathVariable long id) {
        if(pricingRepository.existsById(id)) {
            pricingRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }
}
