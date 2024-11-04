package org.main_java.sistema_monitoreo_jurassic.model.islasDTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.main_java.sistema_monitoreo_jurassic.domain.dinosaurios.Dinosaurio;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public abstract class IslaDTO {
    private String id;
    private String nombre;
    private int capacidadMaxima;
    private int tamanioTablero;
    private int[][] tablero;
    private List<Dinosaurio> dinosaurios;
}
