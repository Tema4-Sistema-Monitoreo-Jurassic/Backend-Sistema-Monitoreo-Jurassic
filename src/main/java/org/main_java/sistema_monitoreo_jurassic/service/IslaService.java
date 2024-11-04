package org.main_java.sistema_monitoreo_jurassic.service;

import org.main_java.sistema_monitoreo_jurassic.domain.dinosaurios.Dinosaurio;
import org.main_java.sistema_monitoreo_jurassic.domain.dinosaurios.Posicion;
import org.main_java.sistema_monitoreo_jurassic.domain.islas.Enfermeria;
import org.main_java.sistema_monitoreo_jurassic.domain.islas.Isla;
import org.main_java.sistema_monitoreo_jurassic.domain.islas.IslaAcuatica;
import org.main_java.sistema_monitoreo_jurassic.domain.islas.IslaTerrestreAerea;
import org.main_java.sistema_monitoreo_jurassic.domain.islas.criaderos.CriaderoAcuatico;
import org.main_java.sistema_monitoreo_jurassic.domain.islas.criaderos.CriaderoTerrestre;
import org.main_java.sistema_monitoreo_jurassic.domain.islas.criaderos.CriaderoVoladores;
import org.main_java.sistema_monitoreo_jurassic.model.dinosauriosDTO.DinosaurioDTO;
import org.main_java.sistema_monitoreo_jurassic.model.islasDTO.EnfermeriaDTO;
import org.main_java.sistema_monitoreo_jurassic.model.islasDTO.IslaAcuaticaDTO;
import org.main_java.sistema_monitoreo_jurassic.model.islasDTO.IslaDTO;
import org.main_java.sistema_monitoreo_jurassic.model.islasDTO.IslaTerrestreAereaDTO;
import org.main_java.sistema_monitoreo_jurassic.model.islasDTO.criaderos.CriaderoAcuaticoDTO;
import org.main_java.sistema_monitoreo_jurassic.model.islasDTO.criaderos.CriaderoTerrestreDTO;
import org.main_java.sistema_monitoreo_jurassic.model.islasDTO.criaderos.CriaderoVoladoresDTO;
import org.main_java.sistema_monitoreo_jurassic.repos.IslaRepository;
import org.main_java.sistema_monitoreo_jurassic.service.factory.IslaFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;


@Service
public class IslaService {

    // Inyectamos el repositorio de islas
    private final IslaRepository islaRepository;
    // Creamos un pool de hilos con 50 hilos
    private final ExecutorService executorService;
    // Creamos un pool de hilos con 50 hilos
    private final ExecutorService executorServiceDelete;
    // Creamos un pool de hilos con 50 hilos
    private final ExecutorService executorServiceCreate;
    // Creamos un pool de hilos con 50 hilos
    private final ExecutorService executorServiceUpdate;
    // Creamos un pool de hilos con 50 hilos
    private final ExecutorService executorServiceGetById;
    // DinosaurioService
    private final DinosaurioService dinosaurioService;
    // IslaFactory
    private final IslaFactory islaFactory;


    // Inyectamos el repositorio de islas y creamos un pool de hilos
    public IslaService(IslaRepository islaRepository, DinosaurioService dinosaurioService, IslaFactory islaFactory) {
        this.islaRepository = islaRepository;
        this.dinosaurioService = dinosaurioService;
        this.islaFactory = islaFactory;
        this.executorService = Executors.newFixedThreadPool(50); // Pool de hilos con 10 hilos
        this.executorServiceDelete = Executors.newFixedThreadPool(50); // Pool de hilos con 50 hilos
        this.executorServiceCreate = Executors.newFixedThreadPool(50); // Pool de hilos con 50 hilos
        this.executorServiceUpdate = Executors.newFixedThreadPool(50); // Pool de hilos con 50 hilos
        this.executorServiceGetById = Executors.newFixedThreadPool(50); // Pool de hilos con 50 hilos
    }


    // Metodo para obtener todas las islas
    public Flux<Isla> getAll() {
        return islaRepository.findAll()
                .subscribeOn(Schedulers.fromExecutor(executorService));
    }

    // Metodo para obtener una isla por su ID
    public Mono<IslaDTO> getById(String id) {
        return islaRepository.findById(id)
                .subscribeOn(Schedulers.fromExecutor(executorServiceGetById))
                .flatMap(this::mapToDTO);
    }

    // Metodo para crear una isla
    public Mono<IslaDTO> create(IslaDTO dto) {
        return mapToEntity(dto)
                .flatMap(isla -> islaRepository.save(isla)
                        .subscribeOn(Schedulers.fromExecutor(executorServiceCreate))
                        .flatMap(this::mapToDTO)
                );
    }

    // Metodo para actualizar una isla
    public Mono<IslaDTO> update(String id, IslaDTO dtoActualizado) {
        return mapToEntity(dtoActualizado)
                .flatMap(islaActualizada ->
                        islaRepository.findById(id)
                                .flatMap(islaExistente -> {
                                    islaExistente.setNombre(islaActualizada.getNombre());
                                    islaExistente.setCapacidadMaxima(islaActualizada.getCapacidadMaxima());
                                    islaExistente.setDinosaurios(islaActualizada.getDinosaurios());
                                    return islaRepository.save(islaExistente);
                                })
                                .subscribeOn(Schedulers.fromExecutor(executorServiceUpdate))
                                .flatMap(this::mapToDTO)
                );
    }

    // Metodo para eliminar una isla
    public Mono<Void> delete(String id) {
        return Mono.fromRunnable(() -> islaRepository.deleteById(id))
                .subscribeOn(Schedulers.fromExecutor(executorServiceDelete))
                .then();
    }


    public Mono<Isla> mapToEntity(IslaDTO dto) {
        return Mono.fromCallable(() -> {
            // Determinamos el tipo de isla en formato String para pasarlo a la fábrica
            String tipo;
            if (dto instanceof IslaTerrestreAereaDTO) {
                tipo = "terrestre-aerea";
            } else if (dto instanceof IslaAcuaticaDTO) {
                tipo = "acuatica";
            } else if (dto instanceof EnfermeriaDTO) {
                tipo = "enfermeria";
            } else if (dto instanceof CriaderoTerrestreDTO) {
                tipo = "criadero-terrestre";
            } else if (dto instanceof CriaderoVoladoresDTO) {
                tipo = "criadero-voladores";
            } else if (dto instanceof CriaderoAcuaticoDTO) {
                tipo = "criadero-acuatico";
            } else {
                throw new IllegalArgumentException("Tipo de IslaDTO desconocido: " + dto.getClass().getSimpleName());
            }

            // Usamos la factoría para crear la instancia de Isla
            Isla isla = IslaFactory.crearIsla(tipo);

            // Configuramos los atributos comunes de Isla
            isla.setId(dto.getId());
            isla.setNombre(dto.getNombre());
            isla.setCapacidadMaxima(dto.getCapacidadMaxima());

            if (isla instanceof IslaTerrestreAerea && dto instanceof IslaTerrestreAereaDTO) {
                IslaTerrestreAerea terrestreAerea = (IslaTerrestreAerea) isla;
                IslaTerrestreAereaDTO terrestreAereaDTO = (IslaTerrestreAereaDTO) dto;
                terrestreAerea.setPermiteTerrestres(terrestreAereaDTO.isPermiteTerrestres());
                terrestreAerea.setPermiteVoladores(terrestreAereaDTO.isPermiteVoladores());
            } else if (isla instanceof IslaAcuatica && dto instanceof IslaAcuaticaDTO) {
                IslaAcuatica acuatica = (IslaAcuatica) isla;
                IslaAcuaticaDTO acuaticaDTO = (IslaAcuaticaDTO) dto;
                acuatica.setPermiteAcuaticos(acuaticaDTO.isPermiteAcuaticos());
            }

            return isla;
        }).subscribeOn(Schedulers.boundedElastic());
    }


    public Mono<IslaDTO> mapToDTO(Isla isla) {
        return Mono.fromCallable(() -> {
            IslaDTO dto;

            if (isla instanceof IslaTerrestreAerea) {
                IslaTerrestreAerea terrestreAerea = (IslaTerrestreAerea) isla;
                IslaTerrestreAereaDTO terrestreAereaDTO = new IslaTerrestreAereaDTO();
                terrestreAereaDTO.setPermiteTerrestres(terrestreAerea.isPermiteTerrestres());
                terrestreAereaDTO.setPermiteVoladores(terrestreAerea.isPermiteVoladores());
                dto = terrestreAereaDTO;
            } else if (isla instanceof IslaAcuatica) {
                dto = new IslaAcuaticaDTO();
            } else if (isla instanceof Enfermeria) {
                dto = new EnfermeriaDTO();
            } else if (isla instanceof CriaderoTerrestre) {
                dto = new CriaderoTerrestreDTO();
            } else if (isla instanceof CriaderoVoladores) {
                dto = new CriaderoVoladoresDTO();
            } else if (isla instanceof CriaderoAcuatico) {
                dto = new CriaderoAcuaticoDTO();
            } else {
                throw new IllegalArgumentException("Tipo de Isla desconocido: " + isla.getClass().getSimpleName());
            }

            // Copia los atributos comunes de Isla a IslaDTO
            dto.setId(isla.getId());
            dto.setNombre(isla.getNombre());
            dto.setCapacidadMaxima(isla.getCapacidadMaxima());

            return dto;
        }).subscribeOn(Schedulers.boundedElastic());
    }

    // Metodo para agregar un dinosaurio a la isla y a la base de datos
    public Mono<Void> agregarDinosaurioIsla(IslaDTO islaDTO, DinosaurioDTO dinoDTO, Posicion posicion) {
        return mapToEntity(islaDTO)
                .flatMap(isla -> {
                    if (isla.esPosicionValida(posicion) && isla.getTablero()[posicion.getX()][posicion.getY()] == 0) {
                        return dinosaurioService.create(dinoDTO) // Devuelve Mono<DinosaurioDTO>
                                .flatMap(dinosaurioService::mapToEntity) // Convierte a Dinosaurio
                                .flatMap(dinosaurio -> {
                                    isla.agregarDinosaurio(dinosaurio, posicion);
                                    return islaRepository.save(isla).then();
                                });
                    } else {
                        return Mono.error(new IllegalArgumentException("Posición no válida o llena"));
                    }
                });
    }

    public Mono<Void> eliminarDinosaurioIsla(IslaDTO islaDTO, String dinosaurioId) {
        return mapToEntity(islaDTO)
                .flatMap(isla -> dinosaurioService.getById(dinosaurioId)
                        .flatMap(dinosaurioService::mapToEntity)
                        .flatMap(dino -> isla.eliminarDinosaurio(dino) // Llamada al metodo reactivo
                                .then(dinosaurioService.delete(dinosaurioId)) // Elimina de la base de datos
                                .then(islaRepository.save(isla).then()) // Guarda la isla actualizada
                        )
                );
    }

    public Mono<Void> iniciarSimulacionMovimiento(IslaDTO islaDTO) {
        return mapToEntity(islaDTO)
                .flatMap(isla -> {
                    Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(() -> {
                        isla.getDinosaurios().forEach(dino -> {
                            Posicion nuevaPosicion = obtenerPosicionAleatoria(isla, dino.getPosicion());
                            if (nuevaPosicion != null) {
                                isla.eliminarDinosaurio(dino);
                                dino.setPosicion(nuevaPosicion);
                                isla.agregarDinosaurio(dino, nuevaPosicion);
                            }
                        });
                        islaRepository.save(isla).subscribe();
                    }, 0, 5, TimeUnit.SECONDS);
                    return Mono.empty();
                });
    }

    private Posicion obtenerPosicionAleatoria(Isla isla, Posicion posicionActual) {
        int[][] direcciones = {{0, 1}, {1, 0}, {0, -1}, {-1, 0}};
        List<Posicion> posiblesPosiciones = new ArrayList<>();

        for (int[] direccion : direcciones) {
            int nuevaX = posicionActual.getX() + direccion[0];
            int nuevaY = posicionActual.getY() + direccion[1];
            Posicion nuevaPosicion = new Posicion(nuevaX, nuevaY, posicionActual.getZona());

            if (isla.esPosicionValida(nuevaPosicion) && isla.getTablero()[nuevaX][nuevaY] == 0) {
                posiblesPosiciones.add(nuevaPosicion);
            }
        }

        if (!posiblesPosiciones.isEmpty()) {
            return posiblesPosiciones.get(new Random().nextInt(posiblesPosiciones.size()));
        }
        return null;
    }

    public Mono<Void> moverDinosaurioIsla(String dinosaurioId, IslaDTO origenDTO, IslaDTO destinoDTO) {
        // Convertimos ambos DTOs a entidades reactivas y las combinamos usando zipWith
        return mapToEntity(origenDTO)
                .zipWith(mapToEntity(destinoDTO))
                .flatMap(tuple -> {
                    Isla origen = tuple.getT1(); // Isla de origen
                    Isla destino = tuple.getT2(); // Isla de destino

                    // Obtenemos el dinosaurio desde el servicio de dinosaurios
                    return dinosaurioService.getById(dinosaurioId)
                            .flatMap(dinosaurioService::mapToEntity)
                            .flatMap(dino -> {
                                // Primero eliminamos el dinosaurio de la isla de origen
                                return origen.eliminarDinosaurio(dino)
                                        .then(destino.agregarDinosaurio(dino, dino.getPosicion())) // Agregamos el dinosaurio a la isla destino
                                        .then(islaRepository.save(origen)) // Guardamos el estado de la isla de origen
                                        .then(islaRepository.save(destino)) // Guardamos el estado de la isla de destino
                                        .then(); // Retornamos Mono<Void> indicando que la operación ha finalizado
                            });
                });
    }
}