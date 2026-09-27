package com.bridgelabz.DigitalLifeInsurancePolicyManagementSystem.jms;

import com.bridgelabz.DigitalLifeInsurancePolicyManagementSystem.repository.PolicyRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class PaymentConsumer {

    private final PolicyRepository policies;

    @JmsListener(destination = "premium.queue")
    public void consume(RenewalMessage message) {

        policies.findById(message.policyId())
                .ifPresent(policy ->
                        log.info(
                                "Premium renewal processing completed for policy {}",
                                policy.getPolicyNumber()
                        )
                );
    }
}