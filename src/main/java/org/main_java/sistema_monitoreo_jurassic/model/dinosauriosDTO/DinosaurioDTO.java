package org.main_java.sistema_monitoreo_jurassic.model.dinosauriosDTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public abstract class DinosaurioDTO {
    private String id;
    private String nombre;
    private int edad;
    private String habitat;
    private PosicionDTO posicion;
}

