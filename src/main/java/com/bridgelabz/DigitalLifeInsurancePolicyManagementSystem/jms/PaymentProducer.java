package com.bridgelabz.DigitalLifeInsurancePolicyManagementSystem.jms;

import com.bridgelabz.DigitalLifeInsurancePolicyManagementSystem.jms.RenewalMessage;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class PaymentProducer {

    private final JmsTemplate jmsTemplate;

    public void sendRenewal(Long policyId) {

        jmsTemplate.convertAndSend(
                "premium.queue",
                new RenewalMessage(policyId)
        );

        log.info(
                "Renewal message sent for policy {}",
                policyId
        );
    }
}