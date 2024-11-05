package org.main_java.sistema_monitoreo_jurassic.domain.dinosaurios.herbivoro;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.main_java.sistema_monitoreo_jurassic.domain.dinosaurios.Dinosaurio;
import org.main_java.sistema_monitoreo_jurassic.domain.dinosaurios.Posicion;
import org.main_java.sistema_monitoreo_jurassic.domain.islas.Isla;
import org.main_java.sistema_monitoreo_jurassic.domain.sensores.Sensor;

import java.util.List;

@Data
@NoArgsConstructor
@Getter
@Setter
public abstract class Herbivoro extends Dinosaurio {

    public Herbivoro(String id, String nombre, int edad, String habitat, List<Sensor> sensores, Posicion posicion, String islaId) {
        super(id, nombre, edad, habitat, sensores, posicion, islaId);
    }

    @Override
    public void comer() {
        System.out.println("El herbívoro está comiendo plantas.");
    }
}
