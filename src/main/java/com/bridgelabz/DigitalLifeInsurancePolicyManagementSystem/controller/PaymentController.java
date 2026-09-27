package com.bridgelabz.DigitalLifeInsurancePolicyManagementSystem.controller;

import java.util.List;

import com.bridgelabz.DigitalLifeInsurancePolicyManagementSystem.dto.PaymentRequest;
import com.bridgelabz.DigitalLifeInsurancePolicyManagementSystem.dto.PaymentResponse;
import com.bridgelabz.DigitalLifeInsurancePolicyManagementSystem.dto.PremiumResponse;
import com.bridgelabz.DigitalLifeInsurancePolicyManagementSystem.service.PaymentService;

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
@RequestMapping("/api")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    // Make a payment
    @PreAuthorize("hasAnyRole('CUSTOMER', 'ADMIN')")
    @PostMapping("/payments")
    public PaymentResponse pay(
            @Valid @RequestBody PaymentRequest request
    ) {

        return paymentService.pay(request);
    }

    // Get one payment
    @PreAuthorize("hasAnyRole('CUSTOMER', 'ADMIN', 'EMPLOYEE')")
    @GetMapping("/payments/{id}")
    public PaymentResponse getPayment(
            @PathVariable Long id
    ) {

        return paymentService.get(id);
    }

    // Get payment history for a policy
    @PreAuthorize("hasAnyRole('CUSTOMER', 'ADMIN', 'EMPLOYEE')")
    @GetMapping("/policies/{policyId}/payments")
    public List<PaymentResponse> getPaymentsByPolicy(
            @PathVariable Long policyId
    ) {

        return paymentService.byPolicy(policyId);
    }

    // Get premiums for a policy
    @PreAuthorize("hasAnyRole('CUSTOMER', 'ADMIN', 'EMPLOYEE')")
    @GetMapping("/policies/{policyId}/premiums")
    public List<PremiumResponse> getPremiums(
            @PathVariable Long policyId
    ) {

        return paymentService.premiums(policyId);
    }

    // Get one premium
    @PreAuthorize("hasAnyRole('CUSTOMER', 'ADMIN', 'EMPLOYEE')")
    @GetMapping("/premiums/{id}")
    public PremiumResponse getPremium(
            @PathVariable Long id
    ) {

        return paymentService.premium(id);
    }
}