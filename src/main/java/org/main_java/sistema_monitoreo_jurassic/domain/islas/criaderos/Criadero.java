package org.main_java.sistema_monitoreo_jurassic.domain.islas.criaderos;

import lombok.*;
import org.main_java.sistema_monitoreo_jurassic.domain.dinosaurios.Dinosaurio;
import org.main_java.sistema_monitoreo_jurassic.domain.islas.Isla;

import java.util.ArrayList;

@Data
@Getter
@Setter
@NoArgsConstructor
public abstract class Criadero extends Isla {

    private boolean permiteMenores;

    @Override
    public abstract void configurarEntorno();
}
