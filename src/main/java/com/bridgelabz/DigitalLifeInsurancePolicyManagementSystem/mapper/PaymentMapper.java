package com.bridgelabz.DigitalLifeInsurancePolicyManagementSystem.mapper;

import com.bridgelabz.DigitalLifeInsurancePolicyManagementSystem.dto.PaymentResponse;
import com.bridgelabz.DigitalLifeInsurancePolicyManagementSystem.dto.PremiumResponse;
import com.bridgelabz.DigitalLifeInsurancePolicyManagementSystem.entity.Payment;
import com.bridgelabz.DigitalLifeInsurancePolicyManagementSystem.entity.Premium;

import org.springframework.stereotype.Component;

@Component
public class PaymentMapper {

    public PaymentResponse toResponse(Payment payment) {

        return new PaymentResponse(
                payment.getId(),
                payment.getPolicy().getId(),
                payment.getPaymentReference(),
                payment.getAmount(),
                payment.getPaymentDate(),
                payment.getStatus().name(),
                payment.getPaymentType().name()
        );
    }

    public PremiumResponse toResponse(Premium premium) {

        return new PremiumResponse(
                premium.getId(),
                premium.getPolicy().getId(),
                premium.getAmount(),
                premium.getDueDate(),
                premium.getPaidDate(),
                premium.getStatus().name()
        );
    }
}