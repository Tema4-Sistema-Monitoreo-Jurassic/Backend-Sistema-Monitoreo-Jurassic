package org.main_java.sistema_monitoreo_jurassic.domain.dinosaurios.omnivoro;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.main_java.sistema_monitoreo_jurassic.domain.dinosaurios.Dinosaurio;

@Data
@NoArgsConstructor
@Getter
@Setter
public abstract class Omnivoro extends Dinosaurio {

    @Override
    public void comer() {
        System.out.println("El omnívoro está comiendo.");
    }

    public void buscarComida() {
        System.out.println("El omnívoro está buscando comida.");
    }

    public void comerPlantas() {
        System.out.println("El omnívoro está comiendo plantas.");
    }

    public void cazar() {
        System.out.println("El omnívoro está cazando.");
    }
}
