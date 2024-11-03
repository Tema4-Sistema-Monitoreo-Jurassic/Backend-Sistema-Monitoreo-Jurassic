package org.main_java.sistema_monitoreo_jurassic.domain.islas.criaderos;

import lombok.*;

@Data
@NoArgsConstructor
public class CriaderoAcuatico extends Criadero {

    public CriaderoAcuatico(String id, String nombre, int capacidadMaxima) {
        super(id, nombre, capacidadMaxima);
    }

    public void permitirDinosauriosAcuaticos() {
        System.out.println("Configurando criadero para dinosaurios acuáticos.");
    }

    @Override
    public void configurarEntorno() {
        permitirDinosauriosAcuaticos();
    }
}
