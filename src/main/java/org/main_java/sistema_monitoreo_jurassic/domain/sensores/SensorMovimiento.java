package org.main_java.sistema_monitoreo_jurassic.domain.sensores;

import lombok.*;

@Data
@NoArgsConstructor
public class SensorMovimiento extends Sensor {

    public SensorMovimiento(String id, double limiteInferior, double limiteSuperior) {
        super(id, "Movimiento", limiteInferior, limiteSuperior);
    }

    public void registrarMovimiento(double velocidad) {
        System.out.println("Movimiento registrado: " + velocidad + " m/s");
        if (estaFueraDeRango(velocidad)) {
            generarEventoFueraDeRango(velocidad);
        }
    }
}
