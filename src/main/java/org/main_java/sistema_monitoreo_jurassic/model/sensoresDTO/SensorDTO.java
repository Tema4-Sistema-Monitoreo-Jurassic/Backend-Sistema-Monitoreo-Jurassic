package org.main_java.sistema_monitoreo_jurassic.model.sensoresDTO;

import lombok.Data;
import org.main_java.sistema_monitoreo_jurassic.domain.Datos;

import java.util.List;

@Data
public abstract class SensorDTO {
    private String id;
    private String tipo;
    private double valor;
    private double limiteInferior;
    private double limiteSuperior;
    private List<Datos> datos;
}