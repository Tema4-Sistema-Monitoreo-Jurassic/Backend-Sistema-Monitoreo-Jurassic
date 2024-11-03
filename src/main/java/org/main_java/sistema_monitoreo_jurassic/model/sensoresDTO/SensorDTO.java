package org.main_java.sistema_monitoreo_jurassic.model.sensoresDTO;

import lombok.Data;

@Data
public abstract class SensorDTO {
    private String id;
    private String tipo;
    private double limiteInferior;
    private double limiteSuperior;
}
