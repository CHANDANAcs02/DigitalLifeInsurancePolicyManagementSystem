package com.bridgelabz.DigitalLifeInsurancePolicyManagementSystem.dto;

import java.time.LocalDate;

public record PremiumResponse(
        Long id,
        Long policyId,
        Double amount,
        LocalDate dueDate,
        LocalDate paidDate,
        String status
) {
}