package com.smartbiz.invoice.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.time.LocalDate;

public record InvoiceRequest(
        @NotNull Long clientId,
        @Positive double amount,
        @Positive double tax,
        @NotNull LocalDate dueDate
) {
}
