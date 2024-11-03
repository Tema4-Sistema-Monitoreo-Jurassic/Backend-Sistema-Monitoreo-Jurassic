package org.main_java.sistema_monitoreo_jurassic.service.factory;

import org.main_java.sistema_monitoreo_jurassic.domain.sensores.*;

public class SensorFactory {

    public static Sensor crearSensor(String id, String tipo, double limiteInferior, double limiteSuperior) {
        switch (tipo.toLowerCase()) {
            case "movimiento":
                return new SensorMovimiento(id, tipo, limiteInferior, limiteSuperior);

            case "temperatura":
                return new SensorTemperatura(id, tipo, limiteInferior, limiteSuperior);

            case "frecuencia-cardiaca":
                return new SensorFrecuenciaCardiaca(id, tipo, limiteInferior, limiteSuperior);

            default:
                throw new IllegalArgumentException("Tipo de sensor no válido: " + tipo);
        }
    }
}

