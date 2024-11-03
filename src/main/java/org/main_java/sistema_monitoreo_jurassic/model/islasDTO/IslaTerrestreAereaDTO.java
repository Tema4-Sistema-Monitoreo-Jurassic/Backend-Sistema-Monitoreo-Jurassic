package org.main_java.sistema_monitoreo_jurassic.model.islasDTO;

import lombok.Data;

@Data
public class IslaTerrestreAereaDTO extends IslaDTO {
    private boolean permiteTerrestres;
    private boolean permiteVoladores;
}
