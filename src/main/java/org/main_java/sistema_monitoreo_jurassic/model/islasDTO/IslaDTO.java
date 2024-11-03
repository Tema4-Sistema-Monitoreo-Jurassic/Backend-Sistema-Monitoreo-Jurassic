package org.main_java.sistema_monitoreo_jurassic.model.islasDTO;

import lombok.Data;

@Data
public abstract class IslaDTO {
    private String id;
    private String nombre;
    private int capacidadMaxima;
}
