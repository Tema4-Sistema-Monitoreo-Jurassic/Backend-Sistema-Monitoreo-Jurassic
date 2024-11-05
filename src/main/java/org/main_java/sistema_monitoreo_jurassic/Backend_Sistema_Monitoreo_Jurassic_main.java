package org.main_java.sistema_monitoreo_jurassic;

import org.main_java.sistema_monitoreo_jurassic.domain.dinosaurios.Posicion;
import org.main_java.sistema_monitoreo_jurassic.domain.dinosaurios.carnivoro.CarnivoroAcuatico;
import org.main_java.sistema_monitoreo_jurassic.domain.dinosaurios.carnivoro.CarnivoroTerrestre;
import org.main_java.sistema_monitoreo_jurassic.domain.dinosaurios.carnivoro.CarnivoroVolador;
import org.main_java.sistema_monitoreo_jurassic.domain.dinosaurios.herbivoro.HerbivoroAcuatico;
import org.main_java.sistema_monitoreo_jurassic.domain.dinosaurios.herbivoro.HerbivoroTerrestre;
import org.main_java.sistema_monitoreo_jurassic.domain.dinosaurios.herbivoro.HerbivoroVolador;
import org.main_java.sistema_monitoreo_jurassic.domain.dinosaurios.omnivoro.OmnivoroAcuatico;
import org.main_java.sistema_monitoreo_jurassic.domain.dinosaurios.omnivoro.OmnivoroTerrestre;
import org.main_java.sistema_monitoreo_jurassic.domain.dinosaurios.omnivoro.OmnivoroVolador;
import org.main_java.sistema_monitoreo_jurassic.model.dinosauriosDTO.DinosaurioDTO;
import org.main_java.sistema_monitoreo_jurassic.model.dinosauriosDTO.omnivoro.OmnivoroAcuaticoDTO;
import org.main_java.sistema_monitoreo_jurassic.model.islasDTO.*;
import org.main_java.sistema_monitoreo_jurassic.model.islasDTO.criaderos.CriaderoAcuaticoDTO;
import org.main_java.sistema_monitoreo_jurassic.model.islasDTO.criaderos.CriaderoTerrestreDTO;
import org.main_java.sistema_monitoreo_jurassic.model.islasDTO.criaderos.CriaderoVoladoresDTO;
import org.main_java.sistema_monitoreo_jurassic.service.DinosaurioService;
import org.main_java.sistema_monitoreo_jurassic.service.IslaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.Collections;
import java.util.Random;
import java.util.UUID;

@SpringBootApplication
public class Backend_Sistema_Monitoreo_Jurassic_main implements CommandLineRunner {

    @Autowired
    private IslaService islaService;
    @Autowired
    private DinosaurioService dinosaurioService;


    public static void main(String[] args) {
        SpringApplication.run(Backend_Sistema_Monitoreo_Jurassic_main.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        // Crear y registrar cada tipo de isla y criadero

        // Isla Acuática
        IslaAcuaticaDTO islaAcuaticaDTO = new IslaAcuaticaDTO(
                "1", "Isla Acuática", 30, 10, new int[10][10], Collections.emptyList()
        );
        islaAcuaticaDTO.setPermiteAcuaticos(true);
        islaService.create(islaAcuaticaDTO).subscribe();

        // Isla Terrestre Aérea
        IslaTerrestreAereaDTO islaTerrestreAereaDTO = new IslaTerrestreAereaDTO(
                "2", "Isla Terrestre Aérea", 40, 15, new int[15][15], Collections.emptyList()
        );
        islaTerrestreAereaDTO.setPermiteTerrestres(true);
        islaTerrestreAereaDTO.setPermiteVoladores(true);
        islaService.create(islaTerrestreAereaDTO).subscribe();

        // Enfermería
        EnfermeriaDTO enfermeriaDTO = new EnfermeriaDTO(
                "3", "Enfermería", 20, 10, new int[10][10], Collections.emptyList()
        );
        islaService.create(enfermeriaDTO).subscribe();

        // Criadero Acuático
        CriaderoAcuaticoDTO criaderoAcuaticoDTO = new CriaderoAcuaticoDTO(
                "4", "Criadero Acuático", 15, 8, new int[8][8], Collections.emptyList()
        );
        islaService.create(criaderoAcuaticoDTO).subscribe();

        // Criadero Terrestre
        CriaderoTerrestreDTO criaderoTerrestreDTO = new CriaderoTerrestreDTO(
                "5", "Criadero Terrestre", 15, 8, new int[8][8], Collections.emptyList()
        );
        islaService.create(criaderoTerrestreDTO).subscribe();

        // Criadero Voladores
        CriaderoVoladoresDTO criaderoVoladoresDTO = new CriaderoVoladoresDTO(
                "6", "Criadero Voladores", 15, 8, new int[8][8], Collections.emptyList()
        );
        islaService.create(criaderoVoladoresDTO).subscribe();

        // Confirmación de creación
        System.out.println("Todas las islas y criaderos han sido creados y registrados.");


        Random random = new Random();

        // Crear y agregar dinosaurios carnívoros
        for (int i = 0; i < 10; i++) {
            String id = UUID.randomUUID().toString();
            int edad = 0;

            CarnivoroAcuatico carnivoroAcuatico = new CarnivoroAcuatico(id, "CarnivoroAcuatico" + i, edad, "acuatico", Collections.emptyList(), new Posicion(random.nextInt(10), random.nextInt(10), "acuatico"));
            dinosaurioService.mapToDTO(carnivoroAcuatico)
                    .flatMap(carnivoroAcuaticoDTO -> islaService.agregarDinosaurioIsla(islaAcuaticaDTO, carnivoroAcuaticoDTO, carnivoroAcuatico.getPosicion()))
                    .subscribe();

            CarnivoroTerrestre carnivoroTerrestre = new CarnivoroTerrestre(UUID.randomUUID().toString(), "CarnivoroTerrestre" + i, edad, "terrestre", Collections.emptyList(), new Posicion(random.nextInt(15), random.nextInt(15), "terrestre"));
            dinosaurioService.mapToDTO(carnivoroTerrestre)
                    .flatMap(carnivoroTerrestreDTO -> islaService.agregarDinosaurioIsla(islaTerrestreAereaDTO, carnivoroTerrestreDTO, carnivoroTerrestre.getPosicion()))
                    .subscribe();

            CarnivoroVolador carnivoroVolador = new CarnivoroVolador(UUID.randomUUID().toString(), "CarnivoroVolador" + i, edad, "volador", Collections.emptyList(), new Posicion(random.nextInt(8), random.nextInt(8), "volador"));
            dinosaurioService.mapToDTO(carnivoroVolador)
                    .flatMap(carnivoroVoladorDTO -> islaService.agregarDinosaurioIsla(criaderoVoladoresDTO, carnivoroVoladorDTO, carnivoroVolador.getPosicion()))
                    .subscribe();
        }



        // Crear y agregar dinosaurios herbívoros
        for (int i = 0; i < 10; i++) {
            int edad = random.nextInt(100);

            HerbivoroAcuatico herbivoroAcuatico = new HerbivoroAcuatico(UUID.randomUUID().toString(), "HerbivoroAcuatico" + i, edad, "acuatico", Collections.emptyList(), new Posicion(random.nextInt(10), random.nextInt(10), "acuatico"));
            dinosaurioService.mapToDTO(herbivoroAcuatico)
                    .flatMap(herbivoroAcuaticoDTO -> islaService.agregarDinosaurioIsla(islaAcuaticaDTO, herbivoroAcuaticoDTO, herbivoroAcuatico.getPosicion()))
                    .subscribe();

            HerbivoroTerrestre herbivoroTerrestre = new HerbivoroTerrestre(UUID.randomUUID().toString(), "HerbivoroTerrestre" + i, edad, "terrestre", Collections.emptyList(), new Posicion(random.nextInt(15), random.nextInt(15), "terrestre"));
            dinosaurioService.mapToDTO(herbivoroTerrestre)
                    .flatMap(herbivoroTerrestreDTO -> islaService.agregarDinosaurioIsla(islaTerrestreAereaDTO, herbivoroTerrestreDTO, herbivoroTerrestre.getPosicion()))
                    .subscribe();

            HerbivoroVolador herbivoroVolador = new HerbivoroVolador(UUID.randomUUID().toString(), "HerbivoroVolador" + i, edad, "volador", Collections.emptyList(), new Posicion(random.nextInt(8), random.nextInt(8), "volador"));
            dinosaurioService.mapToDTO(herbivoroVolador)
                    .flatMap(herbivoroVoladorDTO -> islaService.agregarDinosaurioIsla(criaderoVoladoresDTO, herbivoroVoladorDTO, herbivoroVolador.getPosicion()))
                    .subscribe();
        }



        // Crear y agregar dinosaurios omnívoros
        for (int i = 0; i < 10; i++) {
            int edad = random.nextInt(100);

            OmnivoroAcuatico omnivoroAcuatico = new OmnivoroAcuatico(UUID.randomUUID().toString(), "OmnivoroAcuatico" + i, edad, "acuatico", Collections.emptyList(), new Posicion(random.nextInt(10), random.nextInt(10), "acuatico"));
            dinosaurioService.mapToDTO(omnivoroAcuatico)
                    .flatMap(omnivoroAcuaticoDTO -> islaService.agregarDinosaurioIsla(islaAcuaticaDTO, omnivoroAcuaticoDTO, omnivoroAcuatico.getPosicion()))
                    .subscribe();

            OmnivoroTerrestre omnivoroTerrestre = new OmnivoroTerrestre(UUID.randomUUID().toString(), "OmnivoroTerrestre" + i, edad, "terrestre", Collections.emptyList(), new Posicion(random.nextInt(15), random.nextInt(15), "terrestre"));
            dinosaurioService.mapToDTO(omnivoroTerrestre)
                    .flatMap(omnivoroTerrestreDTO -> islaService.agregarDinosaurioIsla(islaTerrestreAereaDTO, omnivoroTerrestreDTO, omnivoroTerrestre.getPosicion()))
                    .subscribe();

            OmnivoroVolador omnivoroVolador = new OmnivoroVolador(UUID.randomUUID().toString(), "OmnivoroVolador" + i, edad, "volador", Collections.emptyList(), new Posicion(random.nextInt(8), random.nextInt(8), "volador"));
            dinosaurioService.mapToDTO(omnivoroVolador)
                    .flatMap(omnivoroVoladorDTO -> islaService.agregarDinosaurioIsla(criaderoVoladoresDTO, omnivoroVoladorDTO, omnivoroVolador.getPosicion()))
                    .subscribe();
        }


        System.out.println("Todos los dinosaurios han sido creados y agregados a las islas.");
    }

}
