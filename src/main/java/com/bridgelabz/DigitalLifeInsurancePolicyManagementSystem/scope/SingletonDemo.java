package com.bridgelabz.DigitalLifeInsurancePolicyManagementSystem.scope;

import java.util.UUID;

import org.springframework.stereotype.Component;

@Component
public class SingletonDemo {
    private final String instanceId = UUID.randomUUID().toString();
    public String instanceId() { return instanceId; }
}
