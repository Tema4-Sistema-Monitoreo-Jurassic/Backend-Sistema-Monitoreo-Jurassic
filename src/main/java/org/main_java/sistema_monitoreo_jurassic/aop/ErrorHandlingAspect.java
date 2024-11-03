package org.main_java.sistema_monitoreo_jurassic.aop;

import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class ErrorHandlingAspect {

    @AfterThrowing(pointcut = "execution(* org.main_java.sistema_monitoreo_jurassic.service..*(..))", throwing = "error")
    public void handleServiceExceptions(Throwable error) {
        System.err.println("Error capturado en ejecución de servicio: " + error.getMessage());
    }
}

