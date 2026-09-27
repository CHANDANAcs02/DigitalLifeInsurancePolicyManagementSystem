package com.bridgelabz.DigitalLifeInsurancePolicyManagementSystem.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record PolicyRequest(
        @NotNull
        Long customerId,

        @NotNull
        Long planId,

        @Positive
        Double coverageAmount,

        @Positive
        Integer termYears
) {
}