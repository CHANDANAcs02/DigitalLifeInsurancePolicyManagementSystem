package com.bridgelabz.DigitalLifeInsurancePolicyManagementSystem.dto;

import java.time.LocalDateTime;

public record PaymentResponse(
        Long id,
        Long policyId,
        String paymentReference,
        Double amount,
        LocalDateTime paymentDate,
        String status,
        String paymentType
) {
}