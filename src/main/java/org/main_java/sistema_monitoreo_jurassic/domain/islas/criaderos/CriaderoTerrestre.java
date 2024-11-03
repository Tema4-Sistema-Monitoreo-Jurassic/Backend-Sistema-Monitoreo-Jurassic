package org.main_java.sistema_monitoreo_jurassic.domain.islas.criaderos;

import lombok.*;

@Data
@NoArgsConstructor
@Builder
public class CriaderoTerrestre extends Criadero {

    public void permitirDinosauriosTerrestres() {
        System.out.println("Configurando criadero para dinosaurios terrestres.");
    }

    @Override
    public void configurarEntorno() {
        permitirDinosauriosTerrestres();
    }
}
