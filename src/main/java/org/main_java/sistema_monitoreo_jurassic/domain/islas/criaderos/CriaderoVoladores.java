package org.main_java.sistema_monitoreo_jurassic.domain.islas.criaderos;

import lombok.*;

@Data
@NoArgsConstructor
public class CriaderoVoladores extends Criadero {

    public CriaderoVoladores(String id, String nombre, int capacidadMaxima) {
        super(id, nombre, capacidadMaxima);
    }

    public void permitirDinosauriosVoladores() {
        System.out.println("Configurando criadero para dinosaurios voladores.");
    }

    @Override
    public void configurarEntorno() {
        permitirDinosauriosVoladores();
    }
}
