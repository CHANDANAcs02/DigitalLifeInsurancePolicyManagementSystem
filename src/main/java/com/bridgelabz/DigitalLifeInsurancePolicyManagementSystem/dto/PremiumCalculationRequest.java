package com.bridgelabz.DigitalLifeInsurancePolicyManagementSystem.dto;

import jakarta.validation.constraints.Positive;

public record PremiumCalculationRequest(
        @Positive
        int age,

        @Positive
        double coverageAmount,

        @Positive
        int termYears
) {
}