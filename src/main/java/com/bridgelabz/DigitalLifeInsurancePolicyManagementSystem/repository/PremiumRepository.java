package com.bridgelabz.DigitalLifeInsurancePolicyManagementSystem.repository;

import java.util.List;

import com.bridgelabz.DigitalLifeInsurancePolicyManagementSystem.entity.Premium;

import org.springframework.data.jpa.repository.JpaRepository;

public interface PremiumRepository
        extends JpaRepository<Premium, Long> {

    List<Premium> findByPolicyId(Long policyId);
}