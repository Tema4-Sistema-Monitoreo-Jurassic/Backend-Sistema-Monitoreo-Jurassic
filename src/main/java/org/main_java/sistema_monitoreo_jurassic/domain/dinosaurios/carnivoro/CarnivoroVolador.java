package org.main_java.sistema_monitoreo_jurassic.domain.dinosaurios.carnivoro;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.main_java.sistema_monitoreo_jurassic.domain.dinosaurios.Dinosaurio;

@Data
@NoArgsConstructor
@Getter
@Setter
public class CarnivoroVolador extends Carnivoro {

    @Override
    public boolean puedeComer(Dinosaurio otroDino) {
        return true; // Puede comer voladores, terrestres y acuáticos
    }

    @Override
    public boolean estaEnfermo() {
        return false;
    }
}
