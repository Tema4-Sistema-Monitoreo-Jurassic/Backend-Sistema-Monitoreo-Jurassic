package org.main_java.sistema_monitoreo_jurassic.domain.islas.criaderos;

import lombok.*;

@Data
@NoArgsConstructor
@Builder
public class CriaderoAcuatico extends Criadero {

    public void permitirDinosauriosAcuaticos() {
        System.out.println("Configurando criadero para dinosaurios acuáticos.");
    }

    @Override
    public void configurarEntorno() {
        permitirDinosauriosAcuaticos();
    }
}
