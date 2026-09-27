package com.bridgelabz.DigitalLifeInsurancePolicyManagementSystem.scope;

import java.util.UUID;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope("prototype")
public class PrototypeDemo {
    private final String instanceId = UUID.randomUUID().toString();
    public String instanceId() { return instanceId; }
}
