package org.main_java.sistema_monitoreo_jurassic.domain.dinosaurios.omnivoro;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.main_java.sistema_monitoreo_jurassic.domain.dinosaurios.*;
import org.main_java.sistema_monitoreo_jurassic.domain.dinosaurios.carnivoro.CarnivoroAcuatico;
import org.main_java.sistema_monitoreo_jurassic.domain.dinosaurios.carnivoro.CarnivoroTerrestre;
import org.main_java.sistema_monitoreo_jurassic.domain.dinosaurios.herbivoro.HerbivoroAcuatico;
import org.main_java.sistema_monitoreo_jurassic.domain.dinosaurios.herbivoro.HerbivoroTerrestre;

import java.util.Random;

@Data
@NoArgsConstructor
@Getter
@Setter
public abstract class Omnivoro extends Dinosaurio {

    @Override
    public void comer() {
        System.out.println("El omnívoro está comiendo.");
    }

    public void comerPlantas() {
        System.out.println("El omnívoro está comiendo plantas.");
    }

    public void buscarComida(Dinosaurio presa) {
        if (realizarAccionAlAzar()) {
            comerPlantas();
        } else {
            System.out.println("El omnívoro está buscando una presa para cazar.");
            cazar(presa);
        }
    }

    public void cazar(Dinosaurio presa) {
        if (puedeCazar(presa)) {
            if (cazaExitosa()) {
                System.out.println(obtenerMensajeExito());
                eliminarDinosaurio(presa);
            } else {
                System.out.println("La caza no tuvo éxito.");
            }
        } else {
            System.out.println("El omnívoro no puede cazar a este tipo de dinosaurio.");
        }
    }

    private boolean realizarAccionAlAzar() {
        Random random = new Random();
        return random.nextBoolean();
    }

    private boolean puedeCazar(Dinosaurio presa) {
        if (this instanceof OmnivoroAcuatico) {
            return presa instanceof CarnivoroAcuatico || presa instanceof HerbivoroAcuatico || presa instanceof OmnivoroAcuatico;
        } else if (this instanceof OmnivoroTerrestre) {
            return presa instanceof CarnivoroAcuatico || presa instanceof CarnivoroTerrestre ||
                    presa instanceof HerbivoroAcuatico || presa instanceof HerbivoroTerrestre ||
                    presa instanceof OmnivoroAcuatico || presa instanceof OmnivoroTerrestre;
        } else if (this instanceof OmnivoroVolador) {
            return true; // Voladores pueden cazar cualquier tipo
        }
        return false;
    }

    private boolean cazaExitosa() {
        Random random = new Random();
        return random.nextBoolean(); // Determina aleatoriamente si la caza es exitosa
    }

    private String obtenerMensajeExito() {
        if (this instanceof OmnivoroAcuatico) {
            return "El omnívoro acuático ha cazado con éxito a un dinosaurio acuático.";
        } else if (this instanceof OmnivoroTerrestre) {
            return "El omnívoro terrestre ha cazado con éxito a un dinosaurio acuático o terrestre.";
        } else if (this instanceof OmnivoroVolador) {
            return "El omnívoro volador ha cazado con éxito a un dinosaurio de cualquier tipo.";
        }
        return "El omnívoro ha cazado con éxito.";
    }

    private void eliminarDinosaurio(Dinosaurio presa) {
        // Cuando se hagan los services implementar aqui la funcion eliminar dinosaurio para eliminar la presa si es cazada
        System.out.println("El dinosaurio " + presa.getNombre() + " ha sido eliminado del sistema.");
    }
}
