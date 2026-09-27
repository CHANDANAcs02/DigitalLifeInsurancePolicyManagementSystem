package com.bridgelabz.DigitalLifeInsurancePolicyManagementSystem.controller;

import java.util.Map;

import com.bridgelabz.DigitalLifeInsurancePolicyManagementSystem.scope.PrototypeDemo;
import com.bridgelabz.DigitalLifeInsurancePolicyManagementSystem.scope.RequestDemo;
import com.bridgelabz.DigitalLifeInsurancePolicyManagementSystem.scope.SessionDemo;
import com.bridgelabz.DigitalLifeInsurancePolicyManagementSystem.scope.SingletonDemo;

import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/demo/scopes")
@RequiredArgsConstructor
public class ScopeDemoController {

    private final SingletonDemo singletonDemo;
    private final PrototypeDemo prototypeDemo;
    private final RequestDemo requestDemo;
    private final SessionDemo sessionDemo;

    @GetMapping
    public Map<String, String> scopes() {
        return Map.of(
                "singleton", singletonDemo.instanceId(),
                "prototype", prototypeDemo.instanceId(),
                "request", requestDemo.instanceId(),
                "session", sessionDemo.instanceId());
    }
}
