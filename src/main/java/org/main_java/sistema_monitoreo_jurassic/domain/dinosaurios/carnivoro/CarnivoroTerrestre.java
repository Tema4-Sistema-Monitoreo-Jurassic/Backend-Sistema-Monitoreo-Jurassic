package org.main_java.sistema_monitoreo_jurassic.domain.dinosaurios.carnivoro;

import lombok.*;
import org.main_java.sistema_monitoreo_jurassic.domain.dinosaurios.Dinosaurio;
import org.main_java.sistema_monitoreo_jurassic.domain.dinosaurios.herbivoro.HerbivoroAcuatico;
import org.main_java.sistema_monitoreo_jurassic.domain.dinosaurios.herbivoro.HerbivoroTerrestre;
import org.main_java.sistema_monitoreo_jurassic.domain.dinosaurios.omnivoro.OmnivoroAcuatico;
import org.main_java.sistema_monitoreo_jurassic.domain.dinosaurios.omnivoro.OmnivoroTerrestre;
import org.main_java.sistema_monitoreo_jurassic.domain.sensores.SensorFrecuenciaCardiaca;
import org.main_java.sistema_monitoreo_jurassic.domain.sensores.SensorMovimiento;


@Data
@NoArgsConstructor
@Getter
@Setter
@Builder
public class CarnivoroTerrestre extends Carnivoro {

    @Override
    public boolean puedeComer(Dinosaurio otroDino) {
        return otroDino instanceof CarnivoroTerrestre || otroDino instanceof CarnivoroAcuatico ||
                otroDino instanceof HerbivoroTerrestre || otroDino instanceof HerbivoroAcuatico ||
                otroDino instanceof OmnivoroTerrestre || otroDino instanceof OmnivoroAcuatico;
    }

    @Override
    public boolean estaEnfermo(double valorFrecuenciaCardiaca, double valorMovimiento) {
        SensorFrecuenciaCardiaca sensorFC = (SensorFrecuenciaCardiaca) getSensores().stream()
                .filter(sensor -> sensor instanceof SensorFrecuenciaCardiaca)
                .findFirst()
                .orElse(null);

        SensorMovimiento sensorMovimiento = (SensorMovimiento) getSensores().stream()
                .filter(sensor -> sensor instanceof SensorMovimiento)
                .findFirst()
                .orElse(null);

        boolean frecuenciaCardiacaAnormal = sensorFC != null && sensorFC.estaFueraDeRango(valorFrecuenciaCardiaca);
        boolean actividadAnormal = sensorMovimiento != null && sensorMovimiento.estaFueraDeRango(valorMovimiento);

        if (frecuenciaCardiacaAnormal || actividadAnormal) {
            System.out.println("El Carnívoro Terrestre " + getNombre() + " muestra signos de enfermedad.");
            return true;
        }
        return false;
    }
}
