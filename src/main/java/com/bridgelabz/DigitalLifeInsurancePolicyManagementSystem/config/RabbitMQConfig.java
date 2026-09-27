package com.bridgelabz.DigitalLifeInsurancePolicyManagementSystem.config;

import java.util.HashMap;
import java.util.Map;

import org.springframework.amqp.core.AcknowledgeMode;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String EXCHANGE = "policy.exchange";
    public static final String QUEUE = "notification.queue";
    public static final String KEY = "policy.activated";
    public static final String DLX = "policy.dlx";
    public static final String DLQ = "notification.dlq";

    @Bean
    public TopicExchange policyExchange() {
        return new TopicExchange(EXCHANGE, true, false);
    }

    @Bean
    public DirectExchange dlx() {
        return new DirectExchange(DLX, true, false);
    }

    @Bean
    public Queue notificationQueue() {
        Map<String, Object> args = new HashMap<>();
        args.put("x-dead-letter-exchange", DLX);
        args.put("x-dead-letter-routing-key", QUEUE);
        args.put("x-message-ttl", 300000);
        return QueueBuilder.durable(QUEUE).withArguments(args).build();
    }

    @Bean
    public Queue dlq() {
        return QueueBuilder.durable(DLQ).build();
    }

    @Bean
    public Binding notificationBinding() {
        return BindingBuilder.bind(notificationQueue()).to(policyExchange()).with(KEY);
    }

    @Bean
    public Binding dlqBinding() {
        return BindingBuilder.bind(dlq()).to(dlx()).with(QUEUE);
    }

    @Bean
    public JacksonJsonMessageConverter converter() {
        return new JacksonJsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(
            CachingConnectionFactory connectionFactory,
            JacksonJsonMessageConverter converter) {
        connectionFactory.setPublisherConfirmType(CachingConnectionFactory.ConfirmType.CORRELATED);
        connectionFactory.setPublisherReturns(true);

        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(converter);
        template.setMandatory(true);
        template.setConfirmCallback((CorrelationData correlationData, boolean ack, String cause) -> {
            if (!ack && correlationData != null) {
                correlationData.getFuture().completeExceptionally(
                        new IllegalStateException("RabbitMQ publisher confirm failed: " + cause));
            }
        });
        return template;
    }

    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(
            org.springframework.amqp.rabbit.connection.ConnectionFactory connectionFactory,
            JacksonJsonMessageConverter converter) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(converter);
        factory.setAcknowledgeMode(AcknowledgeMode.MANUAL);
        factory.setPrefetchCount(10);
        factory.setDefaultRequeueRejected(false);
        return factory;
    }
}
