package org.main_java.sistema_monitoreo_jurassic.domain.dinosaurios.herbivoro;


import lombok.*;
import org.main_java.sistema_monitoreo_jurassic.domain.sensores.SensorMovimiento;
import org.main_java.sistema_monitoreo_jurassic.domain.sensores.SensorTemperatura;

@Data
@NoArgsConstructor
@Getter
@Setter
@Builder
public class HerbivoroAcuatico extends Herbivoro {

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
            System.out.println("El Herbivoro Acuatico " + getNombre() + " muestra signos de enfermedad.");
            return true;
        }
        return false;
    }
}
