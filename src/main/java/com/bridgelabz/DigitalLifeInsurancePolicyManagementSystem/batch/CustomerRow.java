package com.bridgelabz.DigitalLifeInsurancePolicyManagementSystem.batch;

import java.time.LocalDate;

public record CustomerRow(
        String email,
        String name,
        String phone,
        LocalDate dateOfBirth
) {
}