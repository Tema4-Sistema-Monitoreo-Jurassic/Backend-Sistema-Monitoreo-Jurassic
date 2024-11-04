package org.main_java.sistema_monitoreo_jurassic.domain.islas;

import lombok.*;
import org.main_java.sistema_monitoreo_jurassic.domain.dinosaurios.Dinosaurio;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@Builder
public class Enfermeria extends Isla {

    @Override
    public void configurarEntorno() {
        System.out.println("Entorno de cuidado configurado en la Enfermería.");
    }
}
