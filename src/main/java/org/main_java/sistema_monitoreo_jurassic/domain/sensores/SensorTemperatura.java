package org.main_java.sistema_monitoreo_jurassic.domain.sensores;

import lombok.*;

@Data
@NoArgsConstructor
public class SensorTemperatura extends Sensor {

    public SensorTemperatura(String id, String tipo, double limiteInferior, double limiteSuperior, double valor) {
        super(id, tipo, limiteInferior, limiteSuperior, valor);
    }
    public boolean estaFueraDeRango(double valor) {
        return valor < super.getLimiteInferior() || valor > super.getLimiteSuperior();
    }
}