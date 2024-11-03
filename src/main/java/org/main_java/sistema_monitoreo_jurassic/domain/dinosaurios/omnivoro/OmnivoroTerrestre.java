package org.main_java.sistema_monitoreo_jurassic.domain.dinosaurios.omnivoro;

import lombok.*;

@Data
@NoArgsConstructor
@Getter
@Setter
@Builder
public class OmnivoroTerrestre extends Omnivoro {

    @Override
    public void buscarComida() {
        System.out.println("El omnívoro terrestre está buscando comida.");
    }

    @Override
    public boolean estaEnfermo() {
        return false;
    }

}
