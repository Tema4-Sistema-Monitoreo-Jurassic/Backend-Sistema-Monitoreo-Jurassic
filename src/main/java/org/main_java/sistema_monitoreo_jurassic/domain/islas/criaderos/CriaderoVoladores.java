package org.main_java.sistema_monitoreo_jurassic.domain.islas.criaderos;

import lombok.*;

@Data
@NoArgsConstructor
@Builder
public class CriaderoVoladores extends Criadero {

    public void permitirDinosauriosVoladores() {
        System.out.println("Configurando criadero para dinosaurios voladores.");
    }

    @Override
    public void configurarEntorno() {
        permitirDinosauriosVoladores();
    }
}
