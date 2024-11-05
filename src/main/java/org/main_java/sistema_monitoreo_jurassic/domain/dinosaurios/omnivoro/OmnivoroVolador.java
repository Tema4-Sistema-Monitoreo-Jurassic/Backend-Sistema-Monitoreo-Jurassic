package org.main_java.sistema_monitoreo_jurassic.domain.dinosaurios.omnivoro;


import lombok.*;
import org.main_java.sistema_monitoreo_jurassic.domain.dinosaurios.Posicion;
import org.main_java.sistema_monitoreo_jurassic.domain.islas.Isla;
import org.main_java.sistema_monitoreo_jurassic.domain.sensores.Sensor;
import org.main_java.sistema_monitoreo_jurassic.domain.sensores.SensorFrecuenciaCardiaca;
import org.main_java.sistema_monitoreo_jurassic.domain.sensores.SensorMovimiento;
import org.main_java.sistema_monitoreo_jurassic.domain.sensores.SensorTemperatura;

import java.util.List;
import java.util.Random;

@Data
@NoArgsConstructor
@Getter
@Setter
public class OmnivoroVolador extends Omnivoro {


    public OmnivoroVolador(String id, String nombre, int edad, String habitat, List<Sensor> sensores, Posicion posicion, String islaId) {
        super(id, nombre, edad, habitat, sensores, posicion, islaId);
    }

    @Override
    public boolean estaEnfermo(double valorTemperatura, double valorFrecuenciaCardiaca) {
        // Filtra y encuentra el sensor de temperatura específico de este dinosaurio
        SensorTemperatura sensorTemp = this.getSensores().stream()
                .filter(sensor -> sensor instanceof SensorTemperatura)
                .map(sensor -> (SensorTemperatura) sensor)
                .findFirst()
                .orElse(null);

        // Filtra y encuentra el sensor de frecuencia cardíaca específico de este dinosaurio
        SensorFrecuenciaCardiaca sensorFC = this.getSensores().stream()
                .filter(sensor -> sensor instanceof SensorFrecuenciaCardiaca)
                .map(sensor -> (SensorFrecuenciaCardiaca) sensor)
                .findFirst()
                .orElse(null);

        // Verificar si los valores están fuera de rango
        boolean temperaturaAnormal = sensorTemp != null && sensorTemp.estaFueraDeRango(valorTemperatura);
        boolean frecuenciaCardiacaAnormal = sensorFC != null && sensorFC.estaFueraDeRango(valorFrecuenciaCardiaca);

        // Si alguna de las lecturas está fuera de rango, se considera que el dinosaurio está enfermo
        if (temperaturaAnormal || frecuenciaCardiacaAnormal) {
            System.out.println("El " + getClass().getSimpleName() + " " + getNombre() + " muestra signos de enfermedad.");
            return true;
        }

        // Si no hay lecturas anormales, el dinosaurio no está enfermo
        return false;
    }
}
