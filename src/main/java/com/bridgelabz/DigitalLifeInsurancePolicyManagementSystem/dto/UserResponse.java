package com.bridgelabz.DigitalLifeInsurancePolicyManagementSystem.dto;

public record UserResponse(
        Long id,
        String email,
        String role,
        String token
) {
}
