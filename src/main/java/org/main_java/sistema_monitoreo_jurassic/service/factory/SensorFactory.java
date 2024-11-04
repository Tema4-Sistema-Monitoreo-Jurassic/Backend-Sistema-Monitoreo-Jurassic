package org.main_java.sistema_monitoreo_jurassic.service.factory;

import org.main_java.sistema_monitoreo_jurassic.domain.sensores.*;
import org.springframework.stereotype.Component;


@Component
public class SensorFactory {

    public static Sensor crearSensor(String id, String tipo, double valor, double limiteInferior, double limiteSuperior) {
        switch (tipo.toLowerCase()) {
            case "movimiento":
                return new SensorMovimiento(id, tipo, valor, limiteInferior, limiteSuperior);

            case "temperatura":
                return new SensorTemperatura(id, tipo, valor, limiteInferior, limiteSuperior);

            case "frecuencia-cardiaca":
                return new SensorFrecuenciaCardiaca(id, tipo, valor, limiteInferior, limiteSuperior);

            default:
                throw new IllegalArgumentException("Tipo de sensor no válido: " + tipo);
        }
    }
}

