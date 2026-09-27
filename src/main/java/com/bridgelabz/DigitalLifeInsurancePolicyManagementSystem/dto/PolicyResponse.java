package com.bridgelabz.DigitalLifeInsurancePolicyManagementSystem.dto;

import java.time.LocalDate;

public record PolicyResponse(
        Long id,
        String policyNumber,
        Long customerId,
        Long planId,
        Double coverageAmount,
        Double premiumAmount,
        Integer termYears,
        LocalDate startDate,
        LocalDate endDate,
        String status
) {
}