package org.main_java.sistema_monitoreo_jurassic.model.islasDTO.criaderos;

import lombok.*;
import org.main_java.sistema_monitoreo_jurassic.domain.dinosaurios.Dinosaurio;

import java.util.List;

@Data
@Getter
@Setter@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)

public class CriaderoAcuaticoDTO extends CriaderoDTO {
    public CriaderoAcuaticoDTO(String id, String nombre, int capacidadMaxima, int tamanioTablero, int[][] tablero, List<Dinosaurio> dinosaurios) {
        super(id, nombre, capacidadMaxima, tamanioTablero, tablero, dinosaurios);
    }
}
