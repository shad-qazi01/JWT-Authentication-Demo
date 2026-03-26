package com.smartbiz.auth.dto;

public record AuthResponse(String accessToken, String tokenType, long expiresInSeconds) {
}
