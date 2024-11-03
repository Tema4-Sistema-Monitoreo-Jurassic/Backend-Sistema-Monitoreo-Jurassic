package org.main_java.sistema_monitoreo_jurassic.domain.dinosaurios.herbivoro;


import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@NoArgsConstructor
@Getter
@Setter
public class HerbivoroVolador extends Herbivoro {

    @Override
    public void pastar() {
        System.out.println("El herbívoro volador está pastando.");
    }

    @Override
    public boolean estaEnfermo() {
        return false;
    }

}
