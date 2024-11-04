package org.main_java.sistema_monitoreo_jurassic.domain.sensores;

import lombok.*;
import org.main_java.sistema_monitoreo_jurassic.domain.Datos;
import org.main_java.sistema_monitoreo_jurassic.domain.Evento;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@Getter
@Setter
public abstract class Sensor {
    private String id;
    private String tipo;
    private double valor;
    private double limiteInferior;
    private double limiteSuperior;
    private List<Datos> datos = new ArrayList<>();

    public Sensor(String id, String tipo, double limiteInferior, double limiteSuperior, double valor) {
        this.id = id;
        this.tipo = tipo;
        this.valor = valor;
        this.limiteInferior = limiteInferior;
        this.limiteSuperior = limiteSuperior;
    }

    public Mono<Datos> obtenerDatos() {
        return Mono.just(new Datos());
    }

    public Mono<Void> agregarDato(Datos dato) {
        this.datos.add(dato);
        return Mono.empty();
    }
}