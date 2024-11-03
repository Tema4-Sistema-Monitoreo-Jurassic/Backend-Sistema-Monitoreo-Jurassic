package org.main_java.sistema_monitoreo_jurassic.domain.dinosaurios.herbivoro;


import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@NoArgsConstructor
@Getter
@Setter
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
