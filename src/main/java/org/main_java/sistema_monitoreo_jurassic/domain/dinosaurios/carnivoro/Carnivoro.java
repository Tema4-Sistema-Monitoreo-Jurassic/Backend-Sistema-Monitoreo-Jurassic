package org.main_java.sistema_monitoreo_jurassic.domain.dinosaurios.carnivoro;

import lombok.Getter;
import lombok.Setter;
import org.main_java.sistema_monitoreo_jurassic.domain.dinosaurios.Dinosaurio;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Getter
@Setter
public abstract class Carnivoro extends Dinosaurio {

    @Override
    public void comer() {
        System.out.println("El carnivoro está comiendo carne.");
    }

    public void cazar() {
        System.out.println("El carnivoro está cazando.");
    }

    public abstract boolean puedeComer(Dinosaurio otroDino);
}