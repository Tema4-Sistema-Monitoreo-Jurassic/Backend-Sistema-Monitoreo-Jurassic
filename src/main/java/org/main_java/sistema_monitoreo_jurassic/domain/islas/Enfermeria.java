package org.main_java.sistema_monitoreo_jurassic.domain.islas;

import lombok.*;
import org.main_java.sistema_monitoreo_jurassic.domain.dinosaurios.Dinosaurio;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@Builder
public class Enfermeria extends Isla {

    public void monitorearDinosauriosEnfermos(double temperatura, double frecuenciaCardiaca) {
        getDinosaurios().forEach(dino -> {
            if (dino.estaEnfermo(temperatura, frecuenciaCardiaca)) {
                System.out.println("Dinosaurio enfermo detectado en la enfermería: " + dino.getNombre());
            } else {
                System.out.println("Dinosaurio " + dino.getNombre() + " está sano.");
            }
        });
    }

    public void liberarDinosaurio(Dinosaurio dino) {
        eliminarDinosaurio(dino);
        System.out.println("Dinosaurio " + dino.getNombre() + " liberado de la enfermería.");
    }

    @Override
    public void configurarEntorno() {
        System.out.println("Entorno de cuidado configurado en la Enfermería.");
    }
}
