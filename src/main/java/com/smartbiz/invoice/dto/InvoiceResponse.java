package com.smartbiz.invoice.dto;

import com.smartbiz.invoice.InvoiceStatus;

import java.time.LocalDate;

public record InvoiceResponse(
        Long id,
        Long clientId,
        double amount,
        double tax,
        double total,
        InvoiceStatus status,
        LocalDate dueDate
) {
}
