package org.main_java.sistema_monitoreo_jurassic.domain.dinosaurios.carnivoro;

import lombok.*;
import org.main_java.sistema_monitoreo_jurassic.domain.dinosaurios.Dinosaurio;



@Data
@NoArgsConstructor
@Getter
@Setter
@Builder
public class CarnivoroTerrestre extends Carnivoro {

    @Override
    public boolean puedeComer(Dinosaurio otroDino) {
        return !(otroDino instanceof CarnivoroVolador); // Solo puede comer terrestres y acuáticos
    }

    @Override
    public boolean estaEnfermo() {
        return false;
    }
}
