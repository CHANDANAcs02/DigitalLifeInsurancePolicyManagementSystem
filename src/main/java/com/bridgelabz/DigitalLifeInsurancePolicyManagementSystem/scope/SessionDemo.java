package com.bridgelabz.DigitalLifeInsurancePolicyManagementSystem.scope;

import java.util.UUID;

import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;
import org.springframework.web.context.WebApplicationContext;

@Component
@Scope(value = WebApplicationContext.SCOPE_SESSION, proxyMode = ScopedProxyMode.TARGET_CLASS)
public class SessionDemo {
    private final String instanceId = UUID.randomUUID().toString();
    public String instanceId() { return instanceId; }
}
