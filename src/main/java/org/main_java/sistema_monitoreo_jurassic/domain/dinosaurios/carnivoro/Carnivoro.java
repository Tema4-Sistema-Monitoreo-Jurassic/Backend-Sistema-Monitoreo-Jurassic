package org.main_java.sistema_monitoreo_jurassic.domain.dinosaurios.carnivoro;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.main_java.sistema_monitoreo_jurassic.domain.dinosaurios.Dinosaurio;
import reactor.core.publisher.Mono;
import java.util.Random;

@Data
@NoArgsConstructor
@Getter
@Setter
public abstract class Carnivoro extends Dinosaurio {



    @Override
    public void comer() {
        System.out.println("El carnívoro está comiendo carne.");
    }

    public boolean cazar (Dinosaurio presa) {
        if (puedeComer(presa)) {
            if (cazaExitosa()) {
                System.out.println("La caza fue exitosa. " + getNombre() + " ha cazado a " + presa.getNombre());
                return true;
            } else {
                System.out.println("La caza falló. " + getNombre() + " no logró atrapar a " + presa.getNombre());
                return false;
            }
        } else {
            System.out.println(getNombre() + " no puede cazar a " + presa.getNombre() + " debido a su tipo.");
        }
        return false;
    }

    public abstract boolean puedeComer(Dinosaurio otroDino);

    private boolean cazaExitosa() {
        Random random = new Random();
        return random.nextBoolean(); // 50% de probabilidad de éxito
    }
}
