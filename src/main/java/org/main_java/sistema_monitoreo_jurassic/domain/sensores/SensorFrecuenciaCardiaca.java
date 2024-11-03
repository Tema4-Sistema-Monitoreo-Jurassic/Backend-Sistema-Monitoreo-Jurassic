package org.main_java.sistema_monitoreo_jurassic.domain.sensores;

import lombok.*;

@Data
@NoArgsConstructor
public class SensorFrecuenciaCardiaca extends Sensor {

    public SensorFrecuenciaCardiaca(String id, double limiteInferior, double limiteSuperior) {
        super(id, "Frecuencia Cardiaca", limiteInferior, limiteSuperior);
    }

    public void registrarFrecuenciaCardiaca(double frecuencia) {
        System.out.println("Frecuencia cardiaca registrada: " + frecuencia);
        if (estaFueraDeRango(frecuencia)) {
            generarEventoFueraDeRango(frecuencia);
        }
    }
}

