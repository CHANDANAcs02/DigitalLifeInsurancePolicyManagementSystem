package com.bridgelabz.DigitalLifeInsurancePolicyManagementSystem.controller;

import java.util.List;

import com.bridgelabz.DigitalLifeInsurancePolicyManagementSystem.dto.PolicyResponse;
import com.bridgelabz.DigitalLifeInsurancePolicyManagementSystem.service.PolicyService;

import lombok.RequiredArgsConstructor;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/customers/{customerId}/policies")
@RequiredArgsConstructor
public class CustomerPolicyController {

    private final PolicyService policyService;

    @PreAuthorize("hasAnyRole('CUSTOMER', 'ADMIN', 'EMPLOYEE')")
    @GetMapping
    public List<PolicyResponse> getCustomerPolicies(
            @PathVariable Long customerId
    ) {

        return policyService.customerPolicies(customerId);
    }
}