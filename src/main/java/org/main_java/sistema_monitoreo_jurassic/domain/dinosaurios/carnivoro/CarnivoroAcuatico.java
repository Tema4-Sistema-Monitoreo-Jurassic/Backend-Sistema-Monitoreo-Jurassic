package org.main_java.sistema_monitoreo_jurassic.domain.dinosaurios.carnivoro;

import lombok.*;
import org.main_java.sistema_monitoreo_jurassic.domain.dinosaurios.Dinosaurio;
import org.main_java.sistema_monitoreo_jurassic.domain.sensores.SensorFrecuenciaCardiaca;
import org.main_java.sistema_monitoreo_jurassic.domain.sensores.SensorTemperatura;


@Data
@NoArgsConstructor
@Getter
@Setter
@Builder
public class CarnivoroAcuatico extends Carnivoro {

    @Override
    public boolean puedeComer(Dinosaurio otroDino) {
        return otroDino instanceof CarnivoroAcuatico;
    }

    @Override
    public boolean estaEnfermo(double valorTemperatura, double valorFrecuenciaCardiaca) {
        // Obtener los sensores de temperatura y frecuencia cardíaca
        SensorTemperatura sensorTemp = (SensorTemperatura) getSensores().stream()
                .filter(sensor -> sensor instanceof SensorTemperatura)
                .findFirst()
                .orElse(null);

        SensorFrecuenciaCardiaca sensorFC = (SensorFrecuenciaCardiaca) getSensores().stream()
                .filter(sensor -> sensor instanceof SensorFrecuenciaCardiaca)
                .findFirst()
                .orElse(null);

        // Verificar si los valores están fuera de rango
        boolean temperaturaAnormal = sensorTemp != null && sensorTemp.estaFueraDeRango(valorTemperatura);
        boolean frecuenciaCardiacaAnormal = sensorFC != null && sensorFC.estaFueraDeRango(valorFrecuenciaCardiaca);

        if (temperaturaAnormal || frecuenciaCardiacaAnormal) {
            System.out.println("El Carnívoro Acuático " + getNombre() + " muestra signos de enfermedad.");
            return true;
        }
        return false;
    }
}
