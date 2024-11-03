package org.main_java.sistema_monitoreo_jurassic.domain.dinosaurios;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Data
@AllArgsConstructor
@Getter
@Setter
public class Posicion {
    private int x;
    private int y;
    private String zona;

    public String obtenerCoordenadas() {
        return "Coordenadas: (" + x + ", " + y + ") en la zona " + zona;
    }
}

