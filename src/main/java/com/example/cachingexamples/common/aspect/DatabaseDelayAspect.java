package com.example.cachingexamples.common.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Component
public class DatabaseDelayAspect {

    @Value("${app.database.simulate-delay.enabled:false}")
    private boolean delayEnabled;

    @Value("${app.database.simulate-delay.milliseconds:1000}")
    private long delayMilliseconds;

    @Around("@annotation(com.example.cachingexamples.common.annotation.SimulateDelay)")
    public Object simulateDelay(ProceedingJoinPoint joinPoint) throws Throwable {
        if (delayEnabled) {
            log.debug("Simulating database delay: {}ms", delayMilliseconds);
            try {
                Thread.sleep(delayMilliseconds);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException("Database delay simulation interrupted", e);
            }
        }
        return joinPoint.proceed();
    }
}
