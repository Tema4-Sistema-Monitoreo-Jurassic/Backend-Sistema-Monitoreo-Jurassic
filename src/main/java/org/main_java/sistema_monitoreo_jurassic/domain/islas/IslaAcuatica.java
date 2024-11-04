package org.main_java.sistema_monitoreo_jurassic.domain.islas;

import lombok.*;
import org.main_java.sistema_monitoreo_jurassic.domain.dinosaurios.Dinosaurio;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@Getter
@Setter
public class IslaAcuatica extends Isla {

    private boolean permiteAcuaticos;

    public IslaAcuatica(String id, String nombre, int capacidadMaxima, int[][] tablero, int tamanioTablero, List<Dinosaurio> dinosaurios, boolean permiteAcuaticos) {
        super(id, nombre, capacidadMaxima, tablero, tamanioTablero, dinosaurios);
        this.permiteAcuaticos = permiteAcuaticos;
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
