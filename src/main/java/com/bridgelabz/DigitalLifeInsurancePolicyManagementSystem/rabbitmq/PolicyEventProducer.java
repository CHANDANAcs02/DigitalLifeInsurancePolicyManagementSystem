package com.bridgelabz.DigitalLifeInsurancePolicyManagementSystem.rabbitmq;

import java.util.UUID;

import com.bridgelabz.DigitalLifeInsurancePolicyManagementSystem.config.RabbitMQConfig;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class PolicyEventProducer {

    private final RabbitTemplate rabbitTemplate;

    public void publish(PolicyActivatedEvent event) {
        CorrelationData correlationData = new CorrelationData(UUID.randomUUID().toString());
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.EXCHANGE,
                RabbitMQConfig.KEY,
                event,
                correlationData);
        log.info("Policy activation event published for policy {}", event.policyId());
    }
}
