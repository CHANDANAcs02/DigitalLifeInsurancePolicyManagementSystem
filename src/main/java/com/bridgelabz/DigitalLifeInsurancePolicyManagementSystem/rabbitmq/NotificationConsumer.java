package com.bridgelabz.DigitalLifeInsurancePolicyManagementSystem.rabbitmq;

import java.time.Duration;

import com.rabbitmq.client.Channel;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class NotificationConsumer {

    private final StringRedisTemplate redisTemplate;

    @RabbitListener(
            queues = "notification.queue",
            containerFactory = "rabbitListenerContainerFactory")
    public void consume(
            PolicyActivatedEvent event,
            Channel channel,
            @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag) throws Exception {

        String key = "rabbit:idempotency:policy-activated:" + event.policyId();
        Boolean firstDelivery = redisTemplate.opsForValue()
                .setIfAbsent(key, "PROCESSED", Duration.ofDays(7));

        if (Boolean.FALSE.equals(firstDelivery)) {
            log.info("Duplicate policy activation event ignored for policy {}", event.policyId());
            channel.basicAck(deliveryTag, false);
            return;
        }

        try {
            log.info(
                    "Notification processed for customer {} and policy {}",
                    event.customerId(),
                    event.policyNumber());
            channel.basicAck(deliveryTag, false);
        } catch (Exception ex) {
            redisTemplate.delete(key);
            log.error("Notification processing failed for policy {}", event.policyId(), ex);
            channel.basicNack(deliveryTag, false, false);
        }
    }
}
