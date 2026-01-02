package com.example.carly.repository;

import com.example.carly.model.Invoice;
import lombok.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InvoiceRepository extends JpaRepository<@NonNull Invoice, @NonNull Long> {
}
