package com.bridgelabz.DigitalLifeInsurancePolicyManagementSystem.dto;

import java.time.LocalDate;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record RegisterRequest(
        @NotBlank
        @Email
        String email,

        @NotBlank
        @Size(min = 8)
        String password,

        @NotBlank
        String name,

        @NotBlank
        String phone,

        @NotNull
        LocalDate dateOfBirth
) {
}