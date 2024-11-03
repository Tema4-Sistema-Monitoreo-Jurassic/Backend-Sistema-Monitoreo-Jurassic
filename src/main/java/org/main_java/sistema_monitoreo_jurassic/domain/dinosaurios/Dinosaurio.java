package org.main_java.sistema_monitoreo_jurassic.domain.dinosaurios;

import lombok.*;
import org.main_java.sistema_monitoreo_jurassic.domain.sensores.Sensor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import reactor.core.publisher.Mono;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Document(collection = "dinosaurios")
public abstract class Dinosaurio {
    @Id
    private String id;
    private String nombre;
    private int edad;
    private String habitat;
    private List<Sensor> sensores;
    private Posicion posicion;

    public abstract void comer();

    public void mover(Posicion nuevaPosicion) {
        this.posicion = nuevaPosicion;
    }

    public void monitorearSensores(double valor) {
        sensores.forEach(sensor -> {
            if (sensor.estaFueraDeRango(valor)) {
                enviarAlertaSiSensorFueraDeRango(sensor, valor);
            }
        });
    }

    public void enviarAlertaSiSensorFueraDeRango(Sensor sensor, double valor) {
        System.out.println("Alerta: Sensor " + sensor.getTipo() + " fuera de rango con valor: " + valor);
    }

    public abstract boolean estaEnfermo();
}
