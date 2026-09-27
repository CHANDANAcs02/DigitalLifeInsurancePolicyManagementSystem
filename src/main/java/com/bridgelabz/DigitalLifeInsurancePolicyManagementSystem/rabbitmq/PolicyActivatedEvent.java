package com.bridgelabz.DigitalLifeInsurancePolicyManagementSystem.rabbitmq;

public record PolicyActivatedEvent(
        Long policyId,
        Long customerId,
        String policyNumber
) {
}