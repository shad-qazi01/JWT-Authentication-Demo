package com.smartbiz.invoice;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "INVOICES", schema = "Authentication")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InvoiceEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long clientId;

    private double amount;

    private double tax;      // percentage

    private double total;

    @Enumerated(EnumType.STRING)
    private InvoiceStatus status;

    private LocalDate dueDate;

    private Long createdBy;   // user id


}
