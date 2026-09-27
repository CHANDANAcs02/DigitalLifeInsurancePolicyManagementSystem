package com.bridgelabz.DigitalLifeInsurancePolicyManagementSystem.mapper;

import com.bridgelabz.DigitalLifeInsurancePolicyManagementSystem.dto.PolicyResponse;
import com.bridgelabz.DigitalLifeInsurancePolicyManagementSystem.entity.Policy;

import org.springframework.stereotype.Component;

@Component
public class PolicyMapper {

    public PolicyResponse toResponse(Policy policy) {

        return new PolicyResponse(
                policy.getId(),
                policy.getPolicyNumber(),
                policy.getCustomer().getId(),
                policy.getPlan().getId(),
                policy.getCoverageAmount(),
                policy.getPremiumAmount(),
                policy.getTermYears(),
                policy.getStartDate(),
                policy.getEndDate(),
                policy.getStatus().name()
        );
    }
}