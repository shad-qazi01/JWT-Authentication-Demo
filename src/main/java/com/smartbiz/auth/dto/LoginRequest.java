package com.smartbiz.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.springframework.validation.annotation.Validated;

@Validated
public record LoginRequest(@Email @NotBlank String email, @NotBlank String password) {
}
