package org.main_java.sistema_monitoreo_jurassic.domain.dinosaurios.omnivoro;


import lombok.*;

@Data
@NoArgsConstructor
@Getter
@Setter
@Builder
public class OmnivoroVolador extends Omnivoro {

    @Override
    public void buscarComida() {
        System.out.println("El omnívoro volador está buscando comida.");
    }

    @Override
    public boolean estaEnfermo() {
        return false;
    }

}
