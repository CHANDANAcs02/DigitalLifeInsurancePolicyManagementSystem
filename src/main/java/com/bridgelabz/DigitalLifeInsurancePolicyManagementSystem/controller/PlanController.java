package com.bridgelabz.DigitalLifeInsurancePolicyManagementSystem.controller;

import java.util.List;

import com.bridgelabz.DigitalLifeInsurancePolicyManagementSystem.dto.PlanRequest;
import com.bridgelabz.DigitalLifeInsurancePolicyManagementSystem.dto.PlanResponse;
import com.bridgelabz.DigitalLifeInsurancePolicyManagementSystem.dto.PremiumCalculationRequest;
import com.bridgelabz.DigitalLifeInsurancePolicyManagementSystem.dto.PremiumCalculationResponse;
import com.bridgelabz.DigitalLifeInsurancePolicyManagementSystem.service.PlanService;

import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class PlanController {

    private final PlanService planService;

    // Get all insurance plans
    @GetMapping("/plans")
    public List<PlanResponse> getAllPlans() {

        return planService.all();
    }

    // Get one insurance plan
    @GetMapping("/plans/{id}")
    public PlanResponse getPlan(
            @PathVariable Long id
    ) {

        return planService.get(id);
    }

    // Calculate illustrative premium
    @PostMapping("/plans/calculate-premium")
    public PremiumCalculationResponse calculatePremium(
            @Valid @RequestBody PremiumCalculationRequest request
    ) {

        return planService.calculate(request);
    }

    // Create insurance plan
    @PostMapping("/admin/plans")
    public PlanResponse createPlan(
            @Valid @RequestBody PlanRequest request
    ) {

        return planService.create(request);
    }

    // Update insurance plan
    @PutMapping("/admin/plans/{id}")
    public PlanResponse updatePlan(
            @PathVariable Long id,
            @Valid @RequestBody PlanRequest request
    ) {

        return planService.update(id, request);
    }

    // Delete/deactivate insurance plan
    @DeleteMapping("/admin/plans/{id}")
    public void deletePlan(
            @PathVariable Long id
    ) {

        planService.delete(id);
    }
}