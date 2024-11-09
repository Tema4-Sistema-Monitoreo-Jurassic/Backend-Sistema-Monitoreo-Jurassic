package org.main_java.sistema_monitoreo_jurassic.domain.dinosaurios.carnivoro;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.main_java.sistema_monitoreo_jurassic.domain.dinosaurios.Dinosaurio;
import org.main_java.sistema_monitoreo_jurassic.domain.dinosaurios.Posicion;
import org.main_java.sistema_monitoreo_jurassic.domain.dinosaurios.herbivoro.HerbivoroAcuatico;
import org.main_java.sistema_monitoreo_jurassic.domain.dinosaurios.herbivoro.HerbivoroTerrestre;
import org.main_java.sistema_monitoreo_jurassic.domain.dinosaurios.omnivoro.OmnivoroAcuatico;
import org.main_java.sistema_monitoreo_jurassic.domain.dinosaurios.omnivoro.OmnivoroTerrestre;
import org.main_java.sistema_monitoreo_jurassic.domain.dinosaurios.omnivoro.OmnivoroVolador;
import org.main_java.sistema_monitoreo_jurassic.domain.islas.Isla;
import org.main_java.sistema_monitoreo_jurassic.domain.sensores.Sensor;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Random;

@Data
@NoArgsConstructor
@Getter
@Setter
public abstract class Carnivoro extends Dinosaurio {


    public Carnivoro(String id, String nombre, int edad, String habitat, List<Sensor> sensores, Posicion posicion, String islaId) {
        super(id, nombre, edad, habitat, sensores, posicion, islaId);
    }

    @Override
    public void comer() {
        System.out.println("El carnívoro está comiendo carne.");
    }

    public boolean buscarComida(Dinosaurio presa) {
        boolean alimentarseCarne;

            System.out.println("El omnívoro está buscando una presa para cazar.");
            alimentarseCarne = cazar(presa);

        return alimentarseCarne;
    }

    public boolean cazar (Dinosaurio presa) {
        if (puedeCazarCar(presa)) {
            if (cazaExitosa()) {
                System.out.println(obtenerMensajeExito());
                return true;
            } else {
                System.out.println("La caza no tuvo éxito.");
                return false;
            }
        } else {
            System.out.println("El carnivoro no puede cazar a este tipo de dinosaurio.");
        }
        return false;
    }

    private boolean puedeCazarCar(Dinosaurio presa) {
        if (this instanceof CarnivoroAcuatico) {
            return presa instanceof CarnivoroAcuatico || presa instanceof HerbivoroAcuatico || presa instanceof OmnivoroAcuatico;
        } else if (this instanceof CarnivoroTerrestre) {
            return presa instanceof CarnivoroAcuatico || presa instanceof CarnivoroTerrestre ||
                    presa instanceof HerbivoroAcuatico || presa instanceof HerbivoroTerrestre ||
                    presa instanceof OmnivoroAcuatico || presa instanceof OmnivoroTerrestre;
        } else if (this instanceof CarnivoroVolador) {
            return true; // Voladores pueden cazar cualquier tipo
        }
        return false;
    }


    public abstract boolean puedeComer(Dinosaurio otroDino);

    private boolean cazaExitosa() {
        Random random = new Random();
        return random.nextBoolean(); // 50% de probabilidad de éxito
    }

    private String obtenerMensajeExito() {
        if (this instanceof CarnivoroAcuatico) {
            return "El carnivoro acuático ha cazado con éxito a un dinosaurio acuático.";
        } else if (this instanceof CarnivoroTerrestre) {
            return "El carnivoro terrestre ha cazado con éxito a un dinosaurio acuático o terrestre.";
        } else if (this instanceof CarnivoroVolador) {
            return "El carnivoro volador ha cazado con éxito a un dinosaurio de cualquier tipo.";
        }
        return "El Carnivoro ha cazado con éxito.";
    }
}
