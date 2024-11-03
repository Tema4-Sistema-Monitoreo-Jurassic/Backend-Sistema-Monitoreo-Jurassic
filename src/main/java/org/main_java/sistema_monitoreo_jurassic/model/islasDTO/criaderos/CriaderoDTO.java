package org.main_java.sistema_monitoreo_jurassic.model.islasDTO.criaderos;

import lombok.Data;
import org.main_java.sistema_monitoreo_jurassic.model.islasDTO.IslaDTO;

@Data
public abstract class CriaderoDTO extends IslaDTO {
    private boolean permiteMenores;
}
