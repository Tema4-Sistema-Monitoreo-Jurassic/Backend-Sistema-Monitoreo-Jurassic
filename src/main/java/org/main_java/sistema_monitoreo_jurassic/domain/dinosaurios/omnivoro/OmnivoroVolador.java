package org.main_java.sistema_monitoreo_jurassic.domain.dinosaurios.omnivoro;


import lombok.*;
import org.main_java.sistema_monitoreo_jurassic.domain.sensores.SensorMovimiento;
import org.main_java.sistema_monitoreo_jurassic.domain.sensores.SensorTemperatura;

import java.util.Random;

@Data
@NoArgsConstructor
@Getter
@Setter
@Builder
public class OmnivoroVolador extends Omnivoro {

    @Override
    public boolean estaEnfermo(double valorTemperatura, double valorMovimiento) {
        SensorTemperatura sensorTemp = (SensorTemperatura) getSensores().stream()
                .filter(sensor -> sensor instanceof SensorTemperatura)
                .findFirst()
                .orElse(null);

        SensorMovimiento sensorMovimiento = (SensorMovimiento) getSensores().stream()
                .filter(sensor -> sensor instanceof SensorMovimiento)
                .findFirst()
                .orElse(null);

        boolean temperaturaAnormal = sensorTemp != null && sensorTemp.estaFueraDeRango(valorTemperatura);
        boolean actividadAnormal = sensorMovimiento != null && sensorMovimiento.estaFueraDeRango(valorMovimiento);

        if (temperaturaAnormal || actividadAnormal) {
            System.out.println("El Omnivoro Volador " + getNombre() + " muestra signos de enfermedad.");
            return true;
        }
        return false;
    }
}
