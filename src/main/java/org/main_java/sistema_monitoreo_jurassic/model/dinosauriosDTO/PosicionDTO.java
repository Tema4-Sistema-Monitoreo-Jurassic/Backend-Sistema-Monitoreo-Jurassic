package org.main_java.sistema_monitoreo_jurassic.model.dinosauriosDTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PosicionDTO {
    private int x;
    private int y;
    private String zona;
}
