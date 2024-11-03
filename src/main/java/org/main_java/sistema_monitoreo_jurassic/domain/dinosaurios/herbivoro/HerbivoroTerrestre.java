package org.main_java.sistema_monitoreo_jurassic.domain.dinosaurios.herbivoro;


import lombok.*;

@Data
@NoArgsConstructor
@Getter
@Setter
@Builder
public class HerbivoroTerrestre extends Herbivoro {

    @Override
    public void pastar() {
        System.out.println("El herbívoro terrestre está pastando.");
    }

    @Override
    public boolean estaEnfermo() {
        return false;
    }
}
