package com.smartbiz.invoice;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface InvoiceRepository extends JpaRepository<InvoiceEntity, Long> {

    List<InvoiceEntity> findByCreatedBy(Long userId);

    Optional<InvoiceEntity> findByIdAndCreatedBy(Long id, Long userId);
}
