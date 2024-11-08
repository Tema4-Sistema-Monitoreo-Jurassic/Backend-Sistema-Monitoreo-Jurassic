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
import org.main_java.sistema_monitoreo_jurassic.domain.islas.Isla;
import org.main_java.sistema_monitoreo_jurassic.domain.islas.IslaAcuatica;
import org.main_java.sistema_monitoreo_jurassic.domain.islas.criaderos.CriaderoAcuatico;
import org.main_java.sistema_monitoreo_jurassic.domain.islas.criaderos.CriaderoTerrestre;
import org.main_java.sistema_monitoreo_jurassic.domain.sensores.Sensor;
import org.main_java.sistema_monitoreo_jurassic.domain.sensores.SensorFrecuenciaCardiaca;
import org.main_java.sistema_monitoreo_jurassic.domain.sensores.SensorMovimiento;
import org.main_java.sistema_monitoreo_jurassic.domain.sensores.SensorTemperatura;
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

import java.util.*;

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
                "1", "Isla Acuática", 50, 100, new int[10][10], new ArrayList<>()
        );
        islaAcuaticaDTO.setPermiteAcuaticos(true);
        islaService.create(islaAcuaticaDTO).subscribe();

        // Isla Terrestre Aérea
        IslaTerrestreAereaDTO islaTerrestreAereaDTO = new IslaTerrestreAereaDTO(
                "2", "Isla Terrestre Aérea", 50, 225, new int[15][15], new ArrayList<>()
        );
        islaTerrestreAereaDTO.setPermiteTerrestres(true);
        islaTerrestreAereaDTO.setPermiteVoladores(true);
        islaService.create(islaTerrestreAereaDTO).subscribe();

        // Enfermería
        EnfermeriaDTO enfermeriaDTO = new EnfermeriaDTO(
                "3", "Enfermería", 1, 100, new int[10][10], new ArrayList<>()
        );
        islaService.create(enfermeriaDTO).subscribe();

        // Criadero Acuático
        CriaderoAcuaticoDTO criaderoAcuaticoDTO = new CriaderoAcuaticoDTO(
                "4", "Criadero Acuático", 50, 64, new int[8][8], new ArrayList<>()
        );
        islaService.create(criaderoAcuaticoDTO).subscribe();

        // Criadero Terrestre
        CriaderoTerrestreDTO criaderoTerrestreDTO = new CriaderoTerrestreDTO(
                "5", "Criadero Terrestre", 50, 64, new int[8][8], new ArrayList<>()
        );
        islaService.create(criaderoTerrestreDTO).subscribe();

        // Criadero Voladores
        CriaderoVoladoresDTO criaderoVoladoresDTO = new CriaderoVoladoresDTO(
                "6", "Criadero Voladores", 50, 64, new int[8][8], new ArrayList<>()
        );
        islaService.create(criaderoVoladoresDTO).subscribe();

        // Confirmación de creación
        System.out.println("Todas las islas y criaderos han sido creados y registrados.");

        //------------------------------------------

        CriaderoAcuatico criaderoAcuatico = (CriaderoAcuatico) islaService.mapToEntity(criaderoAcuaticoDTO).block();
        assert criaderoAcuatico != null;
        System.out.println("Isla obtenida: " + criaderoAcuatico.getNombre());

        Random random = new Random();

        // Crear y agregar un dinosaurio carnívoro acuático
        String id = UUID.randomUUID().toString();
        String id2 = UUID.randomUUID().toString();
        int edad = 0;

        // Creación de sensores
        SensorFrecuenciaCardiaca sensorFrecuencia = new SensorFrecuenciaCardiaca(
                UUID.randomUUID().toString(), "FrecuenciaCardiaca", 50.0, 100.0, 75.0
        );

        SensorMovimiento sensorMovimiento = new SensorMovimiento(
                UUID.randomUUID().toString(), "Movimiento", 0.0, 1.0, 0.0
        );

        SensorTemperatura sensorTemperatura = new SensorTemperatura(
                UUID.randomUUID().toString(), "Temperatura", 30.0, 40.0, 35.0
        );

        // Crear una lista de sensores y añadir los sensores creados
        List<Sensor> sensores = new ArrayList<>();
        sensores.add(sensorFrecuencia);
        sensores.add(sensorMovimiento);
        sensores.add(sensorTemperatura);

        // Creación del dinosaurio con los sensores
        CarnivoroAcuatico carnivoroAcuatico = new CarnivoroAcuatico(
                id,
                "carnivoro",
                edad,
                "acuatico",
                sensores,
                new Posicion(random.nextInt(8), random.nextInt(8), "acuatico"),
                criaderoAcuatico.getId()
        );

        // Mapear el dinosaurio a DTO y agregarlo a la isla
        dinosaurioService.mapToDTO(carnivoroAcuatico)
                .flatMap(carnivoroAcuaticoDTO -> islaService.agregarDinosaurioIsla(criaderoAcuatico, carnivoroAcuaticoDTO, carnivoroAcuatico.getPosicion()))
                .doOnSuccess(unused -> System.out.println("Dinosaurio agregado exitosamente."))
                .block(); // Usar block() para esperar la finalización

        System.out.println("Todos los dinosaurios han sido creados y agregados a las islas.");
        System.out.println("EL tablero del criadero acuático es: ");
        System.out.println(Arrays.deepToString(criaderoAcuatico.getTablero()));

        //------------------------------------------

        CriaderoTerrestre criaderoTerrestre = (CriaderoTerrestre) islaService.mapToEntity(criaderoTerrestreDTO).block();
        assert criaderoTerrestre != null;
        System.out.println("Isla obtenida: " + criaderoTerrestre.getNombre());

        // Creación de sensores
        SensorFrecuenciaCardiaca sensorFrecuencia2 = new SensorFrecuenciaCardiaca(
                UUID.randomUUID().toString(), "FrecuenciaCardiaca", 50.0, 100.0, 75.0
        );

        SensorMovimiento sensorMovimiento2 = new SensorMovimiento(
                UUID.randomUUID().toString(), "Movimiento", 0.0, 1.0, 0.0
        );

        SensorTemperatura sensorTemperatura2 = new SensorTemperatura(
                UUID.randomUUID().toString(), "Temperatura", 30.0, 40.0, 35.0
        );

        // Crear una lista de sensores y añadir los sensores creados
        List<Sensor> sensores2 = new ArrayList<>();
        sensores2.add(sensorFrecuencia2);
        sensores2.add(sensorMovimiento2);
        sensores2.add(sensorTemperatura2);

        // Creación del dinosaurio con los sensores
        HerbivoroTerrestre herbivoroTerrestre = new HerbivoroTerrestre(
                id2,
                "herbivoro",
                edad,
                "terrestre",
                sensores2,
                new Posicion(random.nextInt(8), random.nextInt(8), "criadero-terrestre"),
                criaderoTerrestre.getId()
        );

        // Mapear el dinosaurio a DTO y agregarlo a la isla
        dinosaurioService.mapToDTO(herbivoroTerrestre)
                .flatMap(herbivoroTerrestreDTO -> islaService.agregarDinosaurioIsla(criaderoTerrestre, herbivoroTerrestreDTO, herbivoroTerrestre.getPosicion()))
                .doOnSuccess(unused -> System.out.println("Dinosaurio agregado exitosamente."))
                .block(); // Usar block() para esperar la finalización

        System.out.println("Todos los dinosaurios han sido creados y agregados a las islas.");
        System.out.println("EL tablero del criadero terrestre es: ");
        System.out.println(Arrays.deepToString(criaderoTerrestre.getTablero()));
    }

}
