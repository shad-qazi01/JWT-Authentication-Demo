package com.smartbiz.client.dto;

public record ClientResponse(
        Long id,
        String name,
        String email,
        String phone,
        String gst
) {
}
