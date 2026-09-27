package com.bridgelabz.DigitalLifeInsurancePolicyManagementSystem.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
public class LoggingAspect {

    @Around("execution(* com.bridgelabz.DigitalLifeInsurancePolicyManagementSystem.service..*(..))")
    public Object log(ProceedingJoinPoint p) throws Throwable {

        long startTime = System.currentTimeMillis();

        log.info("START {}", p.getSignature().toShortString());

        try {
            return p.proceed();
        } finally {
            log.info(
                    "END {} took {} ms",
                    p.getSignature().toShortString(),
                    System.currentTimeMillis() - startTime
            );
        }
    }
}