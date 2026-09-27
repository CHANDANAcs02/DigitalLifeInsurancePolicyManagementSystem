package com.bridgelabz.DigitalLifeInsurancePolicyManagementSystem.repository;

import com.bridgelabz.DigitalLifeInsurancePolicyManagementSystem.entity.InsurancePlan;

import org.springframework.data.jpa.repository.JpaRepository;

public interface InsurancePlanRepository
        extends JpaRepository<InsurancePlan, Long> {
}