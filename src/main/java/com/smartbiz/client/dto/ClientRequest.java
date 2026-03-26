package com.smartbiz.client.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record ClientRequest(
        @NotBlank String name,
        @Email String email,
        @NotBlank String phone,
        String gst
) {
}
