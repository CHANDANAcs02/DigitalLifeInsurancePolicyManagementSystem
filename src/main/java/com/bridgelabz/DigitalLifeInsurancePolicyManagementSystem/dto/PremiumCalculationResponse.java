package com.bridgelabz.DigitalLifeInsurancePolicyManagementSystem.dto;

public record PremiumCalculationResponse(
        double estimatedPremium,
        String frequency
) {
}