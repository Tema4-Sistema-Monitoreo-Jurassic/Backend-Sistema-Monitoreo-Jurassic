package org.main_java.sistema_monitoreo_jurassic.model.dinosauriosDTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.main_java.sistema_monitoreo_jurassic.domain.sensores.Sensor;
import org.main_java.sistema_monitoreo_jurassic.model.islasDTO.IslaDTO;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public abstract class DinosaurioDTO {
    private String id;
    private String nombre;
    private int edad;
    private String habitat;
    private List<Sensor> sensores;
    private PosicionDTO posicion;
    private String islaId;
}

