package com.bridgelabz.DigitalLifeInsurancePolicyManagementSystem.controller;

import java.util.List;

import com.bridgelabz.DigitalLifeInsurancePolicyManagementSystem.dto.PolicyRequest;
import com.bridgelabz.DigitalLifeInsurancePolicyManagementSystem.dto.PolicyResponse;
import com.bridgelabz.DigitalLifeInsurancePolicyManagementSystem.service.PolicyService;

import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/policies")
@RequiredArgsConstructor
public class PolicyController {

    private final PolicyService policyService;

    // Create a new policy
    @PreAuthorize("hasAnyRole('CUSTOMER', 'ADMIN')")
    @PostMapping
    public PolicyResponse createPolicy(
            @Valid @RequestBody PolicyRequest request
    ) {

        return policyService.create(request);
    }

    // Get all policies
    @PreAuthorize("hasAnyRole('ADMIN', 'EMPLOYEE')")
    @GetMapping
    public List<PolicyResponse> getAllPolicies() {

        return policyService.all();
    }

    // Get one policy
    @PreAuthorize("hasAnyRole('CUSTOMER', 'ADMIN', 'EMPLOYEE')")
    @GetMapping("/{id}")
    public PolicyResponse getPolicy(
            @PathVariable Long id
    ) {

        return policyService.get(id);
    }

    // Get policies for a customer
    @PreAuthorize("hasAnyRole('CUSTOMER', 'ADMIN', 'EMPLOYEE')")
    @GetMapping("/customer/{customerId}")
    public List<PolicyResponse> getCustomerPolicies(
            @PathVariable Long customerId
    ) {

        return policyService.customerPolicies(customerId);
    }

    // Renew policy
    @PreAuthorize("hasAnyRole('CUSTOMER', 'ADMIN')")
    @PostMapping("/{id}/renew")
    public PolicyResponse renewPolicy(
            @PathVariable Long id
    ) {

        return policyService.renew(id);
    }
}