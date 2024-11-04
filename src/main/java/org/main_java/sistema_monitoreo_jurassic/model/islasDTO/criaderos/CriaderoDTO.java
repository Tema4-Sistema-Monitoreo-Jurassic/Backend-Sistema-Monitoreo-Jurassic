package org.main_java.sistema_monitoreo_jurassic.model.islasDTO.criaderos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.main_java.sistema_monitoreo_jurassic.domain.dinosaurios.Dinosaurio;
import org.main_java.sistema_monitoreo_jurassic.model.islasDTO.IslaDTO;

import java.util.List;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public abstract class CriaderoDTO extends IslaDTO {

    public CriaderoDTO(String id, String nombre, int capacidadMaxima, int tamanioTablero, int[][] tablero, List<Dinosaurio> dinosaurios) {
        super(id, nombre, capacidadMaxima, tamanioTablero, tablero, dinosaurios);
    }
}
