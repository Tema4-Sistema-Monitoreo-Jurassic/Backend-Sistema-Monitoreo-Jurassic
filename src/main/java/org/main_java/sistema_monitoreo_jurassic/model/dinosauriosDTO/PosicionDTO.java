package org.main_java.sistema_monitoreo_jurassic.model.dinosauriosDTO;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class PosicionDTO {
    private int x;
    private int y;
    private String zona;
}
