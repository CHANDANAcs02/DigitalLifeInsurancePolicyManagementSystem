package com.bridgelabz.DigitalLifeInsurancePolicyManagementSystem.dto;

import com.bridgelabz.DigitalLifeInsurancePolicyManagementSystem.entity.PaymentType;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record PaymentRequest(
        @NotNull
        Long policyId,

        @Positive
        Double amount,

        @NotNull
        PaymentType paymentType
) {
}