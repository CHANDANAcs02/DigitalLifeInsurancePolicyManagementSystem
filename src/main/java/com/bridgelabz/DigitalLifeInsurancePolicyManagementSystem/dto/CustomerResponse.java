package com.bridgelabz.DigitalLifeInsurancePolicyManagementSystem.dto;

import java.time.LocalDate;

public record CustomerResponse(
        Long id,
        String email,
        String name,
        String phone,
        LocalDate dateOfBirth
) {
}