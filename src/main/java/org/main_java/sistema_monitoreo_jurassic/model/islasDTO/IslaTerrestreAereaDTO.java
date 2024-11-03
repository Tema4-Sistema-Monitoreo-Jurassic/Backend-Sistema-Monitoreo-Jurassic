package org.main_java.sistema_monitoreo_jurassic.model.islasDTO;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class IslaTerrestreAereaDTO extends IslaDTO {
    private boolean permiteTerrestres;
    private boolean permiteVoladores;
}
