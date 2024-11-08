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
import org.main_java.sistema_monitoreo_jurassic.repos.DinosaurioRepository;
import org.main_java.sistema_monitoreo_jurassic.repos.IslaRepository;
import org.main_java.sistema_monitoreo_jurassic.service.factory.IslaFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import reactor.util.function.Tuples;

import java.time.Duration;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;


@Service
public class IslaService {

    // Inyectamos el repositorio de islas
    private final IslaRepository islaRepository;
    //DinosaurioRepository
    private final DinosaurioRepository dinosaurioRepository;
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
    // SensorService
    private final SensorService sensorService;

    private final Set<String> islasConSimulacionActiva = ConcurrentHashMap.newKeySet();


    // Inyectamos el repositorio de islas y creamos un pool de hilos
    public IslaService(IslaRepository islaRepository, DinosaurioService dinosaurioService, DinosaurioRepository dinosaurioRepository, SensorService sensorService) {
        this.islaRepository = islaRepository;
        this.dinosaurioService = dinosaurioService;
        this.sensorService = sensorService;
        this.dinosaurioRepository = dinosaurioRepository;
        this.executorService = Executors.newFixedThreadPool(50); // Pool de hilos con 50 hilos
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
            isla.setDinosaurios(dto.getDinosaurios());
            isla.setTamanioTablero(dto.getTamanioTablero());
            isla.setTablero(dto.getTablero());

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
            dto.setDinosaurios(isla.getDinosaurios());
            dto.setTamanioTablero(isla.getTamanioTablero());
            dto.setTablero(isla.getTablero());

            return dto;
        }).subscribeOn(Schedulers.boundedElastic());
    }

    // Importante llamar este metodo cuando creamos un dinosaurio
    // Metodo para agregar un dinosaurio a la isla y a la base de datos
    public Mono<Void> agregarDinosaurioIsla(Isla isla, DinosaurioDTO dinoDTO, Posicion posicion) {
        if (isla.getTablero()[posicion.getX()][posicion.getY()] == 0) {
            return dinosaurioService.create(dinoDTO) // Devuelve Mono<DinosaurioDTO>
                    .flatMap(dinosaurioService::mapToEntity) // Convierte a Dinosaurio
                    .flatMap(dino -> isla.agregarDinosaurio(dino, posicion) // Llamada al metodo reactivo
                            .then(islaRepository.save(isla).then()) // Guarda la isla actualizada
                    );
        } else {
            return Mono.error(new IllegalArgumentException("Posición no válida o llena"));
        }
    }


    public Mono<Void> eliminarDinosaurioIsla(IslaDTO islaDTO, String dinosaurioId) {
        return mapToEntity(islaDTO)
                .flatMap(isla -> dinosaurioService.getById(dinosaurioId)
                        .flatMap(dinosaurioService::mapToEntity)
                        .flatMap(dino -> isla.eliminarDinosaurio(dino) // Llamada al metodo reactivo
                                .then(islaRepository.save(isla).then()) // Guarda la isla actualizada
                        )
                );
    }

    public Mono<Void> iniciarSimulacionMovimiento(IslaDTO islaDTO) {
        // Verifica si la simulación ya está activa para la isla especificada
        if (islasConSimulacionActiva.contains(islaDTO.getId())) {
            System.out.println("Simulación ya activa para la isla con ID: " + islaDTO.getId());
            return Mono.empty(); // Salir si ya hay una simulación en ejecución para esta isla
        }

        // Agrega la isla al conjunto de simulación activa
        islasConSimulacionActiva.add(islaDTO.getId());

        // Crear y almacenar el token de cancelación
        AtomicBoolean cancelToken = new AtomicBoolean(false);

        dinosaurioService.iniciarSimulacionCancelTokens(islaDTO.getId(), cancelToken);
        return mapToEntity(islaDTO)
                .flatMapMany(isla ->
                        Flux.interval(Duration.ofSeconds(0), Duration.ofSeconds(5)) // Intervalo de movimiento
                                .takeWhile(tick -> !cancelToken.get()) // Monitorea el token de cancelación
                                .concatMap(tick ->
                                        Flux.fromIterable(isla.getDinosaurios())
                                                .concatMap(dino -> {
                                                    Posicion posicionAnterior = dino.getPosicion();
                                                    Posicion nuevaPosicion = obtenerPosicionAleatoria(isla, posicionAnterior);

                                                    if (nuevaPosicion != null) {
                                                        return isla.eliminarDinosaurio(dino) // Remover de la posición anterior
                                                                .then(Mono.fromRunnable(() -> dino.setPosicion(nuevaPosicion))) // Actualizar posición
                                                                .then(isla.agregarDinosaurio(dino, nuevaPosicion)) // Agregar a la nueva posición
                                                                .then(sensorService.detectarYRegistrarMovimiento(dino, posicionAnterior, nuevaPosicion)) // Detectar movimiento
                                                                .then(Mono.zip(
                                                                        dinosaurioRepository.save(dino),
                                                                        islaRepository.save(isla)
                                                                ).then())
                                                                .doOnError(error -> System.err.println("Error moviendo dinosaurio: " + error.getMessage()))
                                                                .onErrorResume(e -> Mono.empty()); // Manejo de errores: continuar con el siguiente dinosaurio
                                                    }
                                                    return Mono.empty();
                                                })
                                )
                                .doFinally(signalType -> {
                                    // Remueve la isla del conjunto de simulación activa y limpia el token de cancelación al finalizar
                                    islasConSimulacionActiva.remove(islaDTO.getId());
                                    dinosaurioService.cancelarSimulacion(islaDTO.getId());
                                    System.out.println("Simulación finalizada para la isla con ID: " + islaDTO.getId());
                                })
                )
                .then();
    }

    private Posicion obtenerPosicionAleatoria(Isla isla, Posicion posicionActual) {
        // Implementación para obtener una nueva posición válida adyacente a la posición actual
        // Asegúrate de que la posición devuelta sea válida y no esté ocupada
        List<Posicion> posicionesDisponibles = new ArrayList<>();
        int x = posicionActual.getX();
        int y = posicionActual.getY();

        // Posibles movimientos: arriba, abajo, izquierda, derecha
        int[][] movimientos = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}};

        for (int[] movimiento : movimientos) {
            int nuevoX = x + movimiento[0];
            int nuevoY = y + movimiento[1];
            Posicion nuevaPosicion = new Posicion(nuevoX, nuevoY, posicionActual.getZona());

            if (isla.esPosicionValida(nuevaPosicion) && isla.getTablero()[nuevoX][nuevoY] == 0) {
                posicionesDisponibles.add(nuevaPosicion);
            }
        }

        if (!posicionesDisponibles.isEmpty()) {
            Random random = new Random();
            return posicionesDisponibles.get(random.nextInt(posicionesDisponibles.size()));
        }

        // Si no hay posiciones disponibles, retorna null
        return null;
    }

    public Mono<Void> moverDinosaurioIsla(String dinosaurioId, IslaDTO origenDTO, IslaDTO destinoDTO) {
        return dinosaurioService.getById(dinosaurioId)
                .flatMap(dinosaurioService::mapToEntity)
                .flatMap(dino -> {
                    // Obtener islas actualizadas desde la base de datos
                    return Mono.zip(
                            islaRepository.findById(origenDTO.getId()),
                            islaRepository.findById(destinoDTO.getId()),
                            (origenIsla, destinoIsla) -> Tuples.of(origenIsla, destinoIsla)
                    ).flatMap(tuple -> {
                        Isla origenIsla = tuple.getT1();
                        Isla destinoIsla = tuple.getT2();

                        // Eliminar dinosaurio de la isla de origen
                        return origenIsla.eliminarDinosaurio(dino)
                                .then(islaRepository.save(origenIsla))
                                .then(destinoIsla.agregarDinosaurio(dino, dino.getPosicion()))
                                .then(islaRepository.save(destinoIsla))
                                // Guardar el dinosaurio actualizado
                                .then(dinosaurioRepository.save(dino))
                                .then();
                    });
                });
    }
}


