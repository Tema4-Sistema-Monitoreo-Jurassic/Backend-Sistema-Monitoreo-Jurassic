package org.main_java.sistema_monitoreo_jurassic.aop;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.main_java.sistema_monitoreo_jurassic.domain.Evento;
import org.main_java.sistema_monitoreo_jurassic.messaging.RabbitMQProducer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class NotificationAspect {

    private static final String EVENTS_QUEUE = "eventsQueue";

    @Autowired
    private RabbitMQProducer rabbitMQProducer;

    @AfterReturning(pointcut = "execution(* org.main_java.sistema_monitoreo_jurassic.service.EventoService.create(..))", returning = "evento")
    public void sendNotifications(JoinPoint joinPoint, Evento evento) {
        String mensajeNotificacion = crearMensajeNotificacion(evento);
        rabbitMQProducer.enviarMensaje(EVENTS_QUEUE, mensajeNotificacion);
        System.out.println("Notificación enviada a RabbitMQ: " + mensajeNotificacion);
    }

    private String crearMensajeNotificacion(Evento evento) {
        String criticidad = (evento.getValor() > 100) ? "CRÍTICO" : "NORMAL";
        return "Evento registrado [" + criticidad + "] - " +
                "Mensaje: " + evento.getMensaje() + ", Valor: " + evento.getValor();
    }
}
