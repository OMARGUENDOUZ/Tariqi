package com.example.carly.repository;

import com.example.carly.model.Payment;
import lombok.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentRepository extends JpaRepository<@NonNull Payment, @NonNull Long> {
}
