package org.main_java.sistema_monitoreo_jurassic.domain.dinosaurios.herbivoro;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.main_java.sistema_monitoreo_jurassic.domain.dinosaurios.Dinosaurio;

@Data
@NoArgsConstructor
@Getter
@Setter
public abstract class Herbivoro extends Dinosaurio {

    @Override
    public void comer() {
        System.out.println("El herbívoro está comiendo plantas.");
    }

    public void pastar() {
        System.out.println("El herbívoro está pastando.");
    }

    public void comerPlantas() {
        System.out.println("El herbívoro está comiendo plantas.");
    }
}
