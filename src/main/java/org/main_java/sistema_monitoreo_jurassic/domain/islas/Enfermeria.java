package org.main_java.sistema_monitoreo_jurassic.domain.islas;

import lombok.*;
import org.main_java.sistema_monitoreo_jurassic.domain.dinosaurios.Dinosaurio;

import java.util.ArrayList;

@Data
@NoArgsConstructor
public class Enfermeria extends Isla {

    public Enfermeria(String id, String nombre, int capacidadMaxima) {
        super(id, nombre, capacidadMaxima, new ArrayList<>());
    }

    public void monitorearDinosauriosEnfermos() {
        getDinosaurios().forEach(dino -> {
            if (dino.estaEnfermo()) {
                System.out.println("Dinosaurio enfermo detectado en la enfermería: " + dino.getNombre());
            }
        });
    }

    public void liberarDinosaurio(Dinosaurio dino) {
        eliminarDinosaurio(dino);
        System.out.println("Dinosaurio " + dino.getNombre() + " liberado de la enfermería.");
    }

    @Override
    public void configurarEntorno() {
        System.out.println("Configurando entorno de cuidado en la Enfermería.");
    }
}
