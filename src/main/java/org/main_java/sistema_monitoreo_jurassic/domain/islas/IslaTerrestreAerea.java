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

    public IslaTerrestreAerea(String id, String nombre, int capacidadMaxima) {
        super(id, nombre, capacidadMaxima, new ArrayList<>());
    }

    public void permitirDinosauriosTerrestres() {
        System.out.println("Configurando entorno para dinosaurios terrestres en Isla Terrestre Aérea.");
    }

    public void permitirDinosauriosVoladores() {
        System.out.println("Configurando entorno para dinosaurios voladores en Isla Terrestre Aérea.");
    }

    @Override
    public void configurarEntorno() {
        permitirDinosauriosTerrestres();
        permitirDinosauriosVoladores();
    }
}

