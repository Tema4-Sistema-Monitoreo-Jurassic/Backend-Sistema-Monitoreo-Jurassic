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

    // Edad mínima para que el dinosaurio sea considerado maduro
    private static final int EDAD_MADURA = 5;

    public abstract void comer();

    public void enviarAlertaSiSensorFueraDeRango(Sensor sensor, double valor) {
        System.out.println("Alerta: Sensor " + sensor.getTipo() + " fuera de rango con valor: " + valor);
    }

    public abstract boolean estaEnfermo(double valorTemperatura, double valorFrecuenciaCardiaca);

    // Metodo para verificar si el dinosaurio ha alcanzado la madurez
    public boolean estaMaduro() {
        return this.edad == EDAD_MADURA;
    }
}
