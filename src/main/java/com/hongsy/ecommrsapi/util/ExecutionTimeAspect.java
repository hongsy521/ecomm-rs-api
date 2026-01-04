package com.hongsy.ecommrsapi.util;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
public class ExecutionTimeAspect {

    @Around("@annotation(LogExecutionTime)")
    public Object logExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();
        Object proceed = joinPoint.proceed(); // 실제 메서드 실행
        long endTime = System.currentTimeMillis();

        log.info("[Execution Time] {} executed in {}ms",
            joinPoint.getSignature(), (endTime - startTime));
        return proceed;
    }
}
