package org.main_java.sistema_monitoreo_jurassic.aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Aspect
@Component
public class PerformanceMonitoringAspect {

    private static final Logger logger = LoggerFactory.getLogger(PerformanceMonitoringAspect.class);

    @Around("execution(* org.main_java.sistema_monitoreo_jurassic.service.*.*(..))")
    public Object monitorPerformance(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();

        // Capturar el nombre del metodo y los argumentos
        String methodName = joinPoint.getSignature().getName();
        String className = joinPoint.getTarget().getClass().getSimpleName();
        Object[] args = joinPoint.getArgs();

        // Log de inicio
        logger.info("Iniciando ejecución del método {}.{} con argumentos: {}", className, methodName, args);

        Object result;
        try {
            // Ejecutar el metodo
            result = joinPoint.proceed();
        } catch (Throwable throwable) {
            // Registrar cualquier excepción que ocurra
            logger.error("Excepción en la ejecución del método {}.{}: {}", className, methodName, throwable.getMessage());
            throw throwable;
        }

        long endTime = System.currentTimeMillis();
        long executionTime = endTime - startTime;

        // Log de fin de ejecución y tiempo tomado
        logger.info("Finalizada ejecución del método {}.{} con resultado: {}, Tiempo de ejecución: {} ms",
                className, methodName, result, executionTime);

        return result;
    }
}

