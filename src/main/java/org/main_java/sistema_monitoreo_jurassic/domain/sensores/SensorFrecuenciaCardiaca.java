package org.main_java.sistema_monitoreo_jurassic.domain.sensores;

import lombok.*;

@Data
@NoArgsConstructor
public class SensorFrecuenciaCardiaca extends Sensor {

    public SensorFrecuenciaCardiaca(String id, String tipo, double limiteInferior, double limiteSuperior) {
        super(id, tipo, limiteInferior, limiteSuperior);
    }

    public void registrarFrecuenciaCardiaca(double frecuencia) {
        System.out.println("Frecuencia cardiaca registrada: " + frecuencia);
        if (estaFueraDeRango(frecuencia)) {
            generarEventoFueraDeRango(frecuencia);
        }
    }
}

