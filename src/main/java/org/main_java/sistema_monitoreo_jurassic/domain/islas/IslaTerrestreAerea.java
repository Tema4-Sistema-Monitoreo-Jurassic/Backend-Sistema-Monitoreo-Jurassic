package org.main_java.sistema_monitoreo_jurassic.domain.islas;

import lombok.*;
import org.main_java.sistema_monitoreo_jurassic.domain.dinosaurios.Dinosaurio;

import java.util.ArrayList;

@Data
@Setter
@Getter
@NoArgsConstructor
public class IslaTerrestreAerea extends Isla {

    private boolean permiteTerrestres;
    private boolean permiteVoladores;

    public IslaTerrestreAerea(String id, String nombre, int capacidadMaxima, int[][] tablero, int tamanioTablero, ArrayList<Dinosaurio> dinosaurios, boolean permiteTerrestres, boolean permiteVoladores) {
        super(id, nombre, capacidadMaxima, tablero, tamanioTablero, dinosaurios);
        this.permiteTerrestres = permiteTerrestres;
        this.permiteVoladores = permiteVoladores;
    }

    public void permitirDinosauriosTerrestres() {
        permiteTerrestres = true;
        System.out.println("Entorno configurado para dinosaurios terrestres en Isla Terrestre Aérea.");
    }

    public void permitirDinosauriosVoladores() {
        permiteVoladores = true;
        System.out.println("Entorno configurado para dinosaurios voladores en Isla Terrestre Aérea.");
    }

    @Override
    public void configurarEntorno() {
        permitirDinosauriosTerrestres();
        permitirDinosauriosVoladores();
    }
}
