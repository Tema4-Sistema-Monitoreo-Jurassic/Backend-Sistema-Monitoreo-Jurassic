package org.main_java.sistema_monitoreo_jurassic.messaging;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class RabbitMQConsumer {

    public Mono<Void> aplicarBackpressureOnDrop(String mensaje) {
        return Flux.just(mensaje)
                .doOnNext(msg -> System.out.println("Procesando mensaje: " + msg))
                .onBackpressureDrop(droppedMsg -> System.out.println("Mensaje descartado por backpressure: " + droppedMsg))
                .then();
    }

    public Mono<Void> aplicarBackpressureOnBuffer(String mensaje) {
        return Flux.just(mensaje)
                .doOnNext(msg -> System.out.println("Procesando mensaje: " + msg))
                .onBackpressureBuffer() // Buffer para manejar la presión de flujo
                .then();
    }
}
