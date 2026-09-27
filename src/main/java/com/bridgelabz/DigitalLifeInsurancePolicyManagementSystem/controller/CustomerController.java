package com.bridgelabz.DigitalLifeInsurancePolicyManagementSystem.controller;

import java.util.List;

import com.bridgelabz.DigitalLifeInsurancePolicyManagementSystem.dto.CustomerResponse;
import com.bridgelabz.DigitalLifeInsurancePolicyManagementSystem.dto.RegisterRequest;
import com.bridgelabz.DigitalLifeInsurancePolicyManagementSystem.service.CustomerService;

import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/customers")
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerService customerService;

    /*
     * Get the currently authenticated customer's details.
     *
     * Example:
     * GET /api/customers/me
     *
     * The logged-in user's email is obtained from the JWT
     * authentication and used to find the matching customer.
     */
    @PreAuthorize("hasAnyRole('CUSTOMER', 'ADMIN')")
    @GetMapping("/me")
    public CustomerResponse getCurrentCustomer(
            Authentication authentication
    ) {

        return customerService.getCurrentCustomer(
                authentication.getName()
        );
    }

    @PreAuthorize("hasAnyRole('CUSTOMER', 'ADMIN')")
    @GetMapping("/{id}")
    public CustomerResponse getCustomer(
            @PathVariable Long id
    ) {

        return customerService.get(id);
    }

    @PreAuthorize("hasAnyRole('CUSTOMER', 'ADMIN')")
    @PutMapping("/{id}")
    public CustomerResponse updateCustomer(
            @PathVariable Long id,
            @Valid @RequestBody RegisterRequest request
    ) {

        return customerService.update(id, request);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'EMPLOYEE')")
    @GetMapping
    public List<CustomerResponse> getAllCustomers() {

        return customerService.all();
    }
}