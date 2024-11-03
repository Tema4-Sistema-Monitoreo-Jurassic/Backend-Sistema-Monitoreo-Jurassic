package org.main_java.sistema_monitoreo_jurassic.domain.dinosaurios.carnivoro;

import lombok.*;
import org.main_java.sistema_monitoreo_jurassic.domain.dinosaurios.Dinosaurio;
import org.main_java.sistema_monitoreo_jurassic.domain.sensores.SensorFrecuenciaCardiaca;
import org.main_java.sistema_monitoreo_jurassic.domain.sensores.SensorMovimiento;
import org.main_java.sistema_monitoreo_jurassic.domain.sensores.SensorTemperatura;

@Data
@NoArgsConstructor
@Getter
@Setter
@Builder
public class CarnivoroVolador extends Carnivoro {

    @Override
    public boolean puedeComer(Dinosaurio otroDino) {
        return true; // Puede comer voladores, terrestres y acuáticos
    }

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
            System.out.println("El Carnívoro Volador " + getNombre() + " muestra signos de enfermedad.");
            return true;
        }
        return false;
    }
}
