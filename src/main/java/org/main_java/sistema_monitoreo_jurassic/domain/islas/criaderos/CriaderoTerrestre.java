package org.main_java.sistema_monitoreo_jurassic.domain.islas.criaderos;

import lombok.*;

@Data
@NoArgsConstructor
public class CriaderoTerrestre extends Criadero {

    public CriaderoTerrestre(String id, String nombre, int capacidadMaxima) {
        super(id, nombre, capacidadMaxima);
    }

    public void permitirDinosauriosTerrestres() {
        System.out.println("Configurando criadero para dinosaurios terrestres.");
    }

    @Override
    public void configurarEntorno() {
        permitirDinosauriosTerrestres();
    }
}
