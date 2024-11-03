package org.main_java.sistema_monitoreo_jurassic.service.factory;

import org.main_java.sistema_monitoreo_jurassic.domain.islas.*;
import org.main_java.sistema_monitoreo_jurassic.domain.islas.criaderos.Criadero;
import org.main_java.sistema_monitoreo_jurassic.domain.islas.criaderos.CriaderoAcuatico;
import org.main_java.sistema_monitoreo_jurassic.domain.islas.criaderos.CriaderoTerrestre;
import org.main_java.sistema_monitoreo_jurassic.domain.islas.criaderos.CriaderoVoladores;

public class IslaFactory {

    public static Isla crearIsla(String tipo) {
        switch (tipo.toLowerCase()) {
            case "terrestre-aerea":
                return new IslaTerrestreAerea();

            case "acuatica":
                return new IslaAcuatica();

            case "enfermeria":
                return new Enfermeria();

            case "criadero":
                return crearCriadero(tipo);

            default:
                throw new IllegalArgumentException("Tipo de isla no válido: " + tipo);
        }
    }

    private static Criadero crearCriadero(String tipo) {
        switch (tipo.toLowerCase()) {
            case "criadero-terrestre":
                return new CriaderoTerrestre();

            case "criadero-voladores":
                return new CriaderoVoladores();

            case "criadero-acuatico":
                return new CriaderoAcuatico();

            default:
                throw new IllegalArgumentException("Tipo de criadero no válido: " + tipo);
        }
    }
}
