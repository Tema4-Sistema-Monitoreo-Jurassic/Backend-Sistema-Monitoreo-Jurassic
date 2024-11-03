package org.main_java.sistema_monitoreo_jurassic.domain.dinosaurios.herbivoro;


import lombok.*;

@Data
@NoArgsConstructor
@Getter
@Setter
@Builder
public class HerbivoroAcuatico extends Herbivoro {

    @Override
    public void pastar() {
        System.out.println("El herbívoro acuático está alimentandose.");
    }

    @Override
    public boolean estaEnfermo() {
        return false;
    }
}
