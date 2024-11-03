package org.main_java.sistema_monitoreo_jurassic.domain.dinosaurios.herbivoro;


import lombok.*;

@Data
@NoArgsConstructor
@Getter
@Setter
@Builder
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
