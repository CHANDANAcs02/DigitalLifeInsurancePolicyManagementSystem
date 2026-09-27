package com.bridgelabz.DigitalLifeInsurancePolicyManagementSystem.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

public record PlanRequest(
        @NotBlank
        String name,

        String description,

        @Positive
        Double coverageAmount,

        @Positive
        Integer termYears,

        @Positive
        Double basePremium
) {
}