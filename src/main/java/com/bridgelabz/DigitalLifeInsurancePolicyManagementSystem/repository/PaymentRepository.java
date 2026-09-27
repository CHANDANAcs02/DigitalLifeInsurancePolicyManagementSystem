package com.bridgelabz.DigitalLifeInsurancePolicyManagementSystem.repository;

import java.util.List;
import java.util.Optional;

import com.bridgelabz.DigitalLifeInsurancePolicyManagementSystem.entity.Payment;

import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository
        extends JpaRepository<Payment, Long> {

    List<Payment> findByPolicyId(Long policyId);

    Optional<Payment> findByPaymentReference(String reference);
}