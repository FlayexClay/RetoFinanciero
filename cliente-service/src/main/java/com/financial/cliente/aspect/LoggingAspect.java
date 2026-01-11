package com.financial.cliente.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;

@Aspect
@Component
@Slf4j
public class LoggingAspect {

    @Pointcut("within(com.financial.cliente.service..*)")
    public void serviceMethods() {}

    @Pointcut("within(com.financial.cliente.controller..*)")
    public void controllerMethods() {}

    @Around("serviceMethods() || controllerMethods()" )
    public  Object logMethodExecution(ProceedingJoinPoint joinPoint) throws Throwable{
        String className = joinPoint.getSignature().getDeclaringTypeName();
        String methodName = joinPoint.getSignature().getName();
        Object[] args = joinPoint.getArgs();

        log.info("Ejecutando: {}.{} con argumentos: {}",
                className, methodName, Arrays.toString(args));

        Instant start = Instant.now();
        try {
            Object result = joinPoint.proceed();
            Instant end = Instant.now();
            Duration duration = Duration.between(start, end);

            log.info("Completado: {}.{} en {} ms",
                className, methodName, duration.toMillis());

            return result;
        }catch (Exception e){
            log.error("Error en: {}.{} - {}",
                    className, methodName, e.getMessage());
            throw e;
        }
    }
}
