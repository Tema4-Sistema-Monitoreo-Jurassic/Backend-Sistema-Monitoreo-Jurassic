package org.main_java.sistema_monitoreo_jurassic.domain.sensores;

import lombok.*;
import org.main_java.sistema_monitoreo_jurassic.domain.Datos;
import org.main_java.sistema_monitoreo_jurassic.domain.Evento;
import reactor.core.publisher.Mono;

@Data
@NoArgsConstructor
@Getter
@Setter
public abstract class Sensor {
    private String id;
    private String tipo;
    private double limiteInferior;
    private double limiteSuperior;

    public Sensor(String id, String tipo, double limiteInferior, double limiteSuperior) {
        this.id = id;
        this.tipo = tipo;
        this.limiteInferior = limiteInferior;
        this.limiteSuperior = limiteSuperior;
    }

    public Mono<Datos> obtenerDatos() {
        return Mono.just(new Datos());
    }

    public boolean estaFueraDeRango(double valor) {
        return valor < limiteInferior || valor > limiteSuperior;
    }

    public Evento generarEventoFueraDeRango(double valor) {
        if (estaFueraDeRango(valor)) {
            return new Evento("Sensor fuera de rango: " + tipo, valor);
        }
        return null;
    }
}

