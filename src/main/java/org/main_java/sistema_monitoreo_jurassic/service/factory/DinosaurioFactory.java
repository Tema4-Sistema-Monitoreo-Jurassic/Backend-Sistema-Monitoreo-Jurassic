package org.main_java.sistema_monitoreo_jurassic.service.factory;

import org.main_java.sistema_monitoreo_jurassic.domain.dinosaurios.*;
import org.main_java.sistema_monitoreo_jurassic.domain.dinosaurios.carnivoro.Carnivoro;
import org.main_java.sistema_monitoreo_jurassic.domain.dinosaurios.carnivoro.CarnivoroAcuatico;
import org.main_java.sistema_monitoreo_jurassic.domain.dinosaurios.carnivoro.CarnivoroTerrestre;
import org.main_java.sistema_monitoreo_jurassic.domain.dinosaurios.carnivoro.CarnivoroVolador;
import org.main_java.sistema_monitoreo_jurassic.domain.dinosaurios.herbivoro.Herbivoro;
import org.main_java.sistema_monitoreo_jurassic.domain.dinosaurios.herbivoro.HerbivoroAcuatico;
import org.main_java.sistema_monitoreo_jurassic.domain.dinosaurios.herbivoro.HerbivoroTerrestre;
import org.main_java.sistema_monitoreo_jurassic.domain.dinosaurios.herbivoro.HerbivoroVolador;
import org.main_java.sistema_monitoreo_jurassic.domain.dinosaurios.omnivoro.Omnivoro;
import org.main_java.sistema_monitoreo_jurassic.domain.dinosaurios.omnivoro.OmnivoroAcuatico;
import org.main_java.sistema_monitoreo_jurassic.domain.dinosaurios.omnivoro.OmnivoroTerrestre;
import org.main_java.sistema_monitoreo_jurassic.domain.dinosaurios.omnivoro.OmnivoroVolador;
import org.springframework.stereotype.Component;

@Component
public class DinosaurioFactory {

    public static Dinosaurio crearDinosaurio(String tipo, String habitat) {
        switch (tipo.toLowerCase()) {
            case "carnivoro":
                return crearCarnivoro(habitat);

            case "herbivoro":
                return crearHerbivoro(habitat);

            case "omnivoro":
                return crearOmnivoro(habitat);

            default:
                throw new IllegalArgumentException("Tipo de dinosaurio no válido: " + tipo);
        }
    }

    private static Carnivoro crearCarnivoro(String habitat) {
        switch (habitat.toLowerCase()) {
            case "volador":
                return new CarnivoroVolador();

            case "terrestre":
                return new CarnivoroTerrestre();

            case "acuatico":
                return new CarnivoroAcuatico();

            default:
                throw new IllegalArgumentException("Hábitat no válido para carnívoro: " + habitat);
        }
    }

    private static Herbivoro crearHerbivoro(String habitat) {
        switch (habitat.toLowerCase()) {
            case "volador":
                return new HerbivoroVolador();

            case "terrestre":
                return new HerbivoroTerrestre();

            case "acuatico":
                return new HerbivoroAcuatico();

            default:
                throw new IllegalArgumentException("Hábitat no válido para herbívoro: " + habitat);
        }
    }

    private static Omnivoro crearOmnivoro(String habitat) {
        switch (habitat.toLowerCase()) {
            case "volador":
                return new OmnivoroVolador();

            case "terrestre":
                return new OmnivoroTerrestre();

            case "acuatico":
                return new OmnivoroAcuatico();

            default:
                throw new IllegalArgumentException("Hábitat no válido para omnívoro: " + habitat);
        }
    }
}

