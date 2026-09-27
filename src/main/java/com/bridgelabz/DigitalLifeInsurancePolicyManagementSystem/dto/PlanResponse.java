package com.bridgelabz.DigitalLifeInsurancePolicyManagementSystem.dto;

public record PlanResponse(
        Long id,
        String name,
        String description,
        Double coverageAmount,
        Integer termYears,
        Double basePremium,
        String status
) {
}