package org.main_java.sistema_monitoreo_jurassic.domain.islas;

import lombok.*;

import java.util.ArrayList;

@Data
@NoArgsConstructor
@Getter
@Setter
public class IslaAcuatica extends Isla {

    private boolean permiteAcuaticos;

    public IslaAcuatica(String id, String nombre, int capacidadMaxima) {
        super(id, nombre, capacidadMaxima, new ArrayList<>());
    }

    public void permitirDinosauriosAcuaticos() {
        permiteAcuaticos = true;
        System.out.println("Entorno configurado para dinosaurios acuáticos en Isla Acuática.");
    }

    @Override
    public void configurarEntorno() {
        permitirDinosauriosAcuaticos();
    }
}
