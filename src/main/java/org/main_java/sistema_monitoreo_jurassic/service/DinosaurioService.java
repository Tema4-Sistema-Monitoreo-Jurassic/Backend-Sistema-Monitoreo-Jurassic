package org.main_java.sistema_monitoreo_jurassic.service;

import org.main_java.sistema_monitoreo_jurassic.domain.dinosaurios.Dinosaurio;
import org.main_java.sistema_monitoreo_jurassic.domain.dinosaurios.Posicion;
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
import org.main_java.sistema_monitoreo_jurassic.domain.islas.Enfermeria;
import org.main_java.sistema_monitoreo_jurassic.domain.islas.Isla;
import org.main_java.sistema_monitoreo_jurassic.domain.islas.IslaAcuatica;
import org.main_java.sistema_monitoreo_jurassic.domain.islas.IslaTerrestreAerea;
import org.main_java.sistema_monitoreo_jurassic.domain.islas.criaderos.Criadero;
import org.main_java.sistema_monitoreo_jurassic.domain.islas.criaderos.CriaderoAcuatico;
import org.main_java.sistema_monitoreo_jurassic.domain.islas.criaderos.CriaderoTerrestre;
import org.main_java.sistema_monitoreo_jurassic.domain.islas.criaderos.CriaderoVoladores;
import org.main_java.sistema_monitoreo_jurassic.messaging.RabbitMQProducer;
import org.main_java.sistema_monitoreo_jurassic.model.dinosauriosDTO.DinosaurioDTO;
import org.main_java.sistema_monitoreo_jurassic.model.dinosauriosDTO.PosicionDTO;
import org.main_java.sistema_monitoreo_jurassic.model.dinosauriosDTO.carnivoro.CarnivoroAcuaticoDTO;
import org.main_java.sistema_monitoreo_jurassic.model.dinosauriosDTO.carnivoro.CarnivoroTerrestreDTO;
import org.main_java.sistema_monitoreo_jurassic.model.dinosauriosDTO.carnivoro.CarnivoroVoladorDTO;
import org.main_java.sistema_monitoreo_jurassic.model.dinosauriosDTO.herbivoro.HerbivoroAcuaticoDTO;
import org.main_java.sistema_monitoreo_jurassic.model.dinosauriosDTO.herbivoro.HerbivoroTerrestreDTO;
import org.main_java.sistema_monitoreo_jurassic.model.dinosauriosDTO.herbivoro.HerbivoroVoladorDTO;
import org.main_java.sistema_monitoreo_jurassic.model.dinosauriosDTO.omnivoro.OmnivoroAcuaticoDTO;
import org.main_java.sistema_monitoreo_jurassic.model.dinosauriosDTO.omnivoro.OmnivoroTerrestreDTO;
import org.main_java.sistema_monitoreo_jurassic.model.dinosauriosDTO.omnivoro.OmnivoroVoladorDTO;
import org.main_java.sistema_monitoreo_jurassic.model.islasDTO.EnfermeriaDTO;
import org.main_java.sistema_monitoreo_jurassic.model.islasDTO.IslaAcuaticaDTO;
import org.main_java.sistema_monitoreo_jurassic.model.islasDTO.IslaDTO;
import org.main_java.sistema_monitoreo_jurassic.model.islasDTO.IslaTerrestreAereaDTO;
import org.main_java.sistema_monitoreo_jurassic.model.islasDTO.criaderos.CriaderoAcuaticoDTO;
import org.main_java.sistema_monitoreo_jurassic.model.islasDTO.criaderos.CriaderoDTO;
import org.main_java.sistema_monitoreo_jurassic.model.islasDTO.criaderos.CriaderoTerrestreDTO;
import org.main_java.sistema_monitoreo_jurassic.model.islasDTO.criaderos.CriaderoVoladoresDTO;
import org.main_java.sistema_monitoreo_jurassic.repos.DinosaurioRepository;
import org.main_java.sistema_monitoreo_jurassic.repos.IslaRepository;
import org.main_java.sistema_monitoreo_jurassic.service.factory.DinosaurioFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;


@Service
public class DinosaurioService {

    // Inyectamos el productor de RabbitMQ
    private final RabbitMQProducer rabbitMQProducer;
    // Inyectamos el repositorio de dinosaurios
    private final DinosaurioRepository dinosaurioRepository;
    // Inyectamos el repositorio de islas
    private final IslaRepository islaRepository;
    // Creamos un pool de hilos con 50 hilos
    private final ExecutorService executorService;
    // Creamos un pool de hilos con 50 hilos
    private final ExecutorService executorServiceTipo;
    // Creamos un pool de hilos con 50 hilos
    private final ExecutorService executorServiceDelete;
    // Creamos un pool de hilos con 50 hilos
    private final ExecutorService executorServiceCreate;
    // Creamos un pool de hilos con 50 hilos
    private final ExecutorService executorServiceUpdate;
    // Creamos un pool de hilos con 50 hilos
    private final ExecutorService executorServiceGetById;
    // Inyectamos el servicio de islas
    private final IslaService islaService;
    // Inyectamos el servicio de sensores
    private final SensorService sensorService;
    // Bloqueo para sincronización de tareas críticas
    private final ReentrantLock lock = new ReentrantLock(); // Bloqueo para sincronización de tareas críticas




    // Inyectamos el repositorio de dinosaurios, el factory de dinosaurios y el productor de RabbitMQ
    @Autowired
    public DinosaurioService(DinosaurioRepository dinosaurioRepository,
                             IslaRepository islaRepository,
                             RabbitMQProducer rabbitMQProducer,
                             @Lazy IslaService islaService,
                             SensorService sensorService) {
        this.islaService = islaService;
        this.islaRepository = islaRepository;
        this.dinosaurioRepository = dinosaurioRepository;
        this.rabbitMQProducer = rabbitMQProducer;
        this.sensorService = sensorService;
        this.executorService = Executors.newFixedThreadPool(50);
        this.executorServiceTipo = Executors.newFixedThreadPool(50);
        this.executorServiceDelete = Executors.newFixedThreadPool(50);
        this.executorServiceCreate = Executors.newFixedThreadPool(50);
        this.executorServiceUpdate = Executors.newFixedThreadPool(50);
        this.executorServiceGetById = Executors.newFixedThreadPool(50);
    }


    // Metodo para obtener todos los dinosaurios
    public Flux<Dinosaurio> getAll() {
        return dinosaurioRepository.findAll()
                .subscribeOn(Schedulers.fromExecutor(executorService));
    }

    // Metodo general para obtener subtipos de dinosaurios usando filtrado concurrente
    private <T extends Dinosaurio> Flux<T> obtenerDinosauriosPorTipo(Class<T> tipoClase) {
        return getAll()
                .filter(dino -> tipoClase.isInstance(dino))
                .cast(tipoClase)
                .subscribeOn(Schedulers.fromExecutor(executorServiceTipo));
    }

// Funciones específicas para obtener cada subtipo de dinosaurio

    public Flux<CarnivoroTerrestre> obtenerCarnivorosTerrestres() {
        return obtenerDinosauriosPorTipo(CarnivoroTerrestre.class);
    }

    // Metodo para obtener todos los dinosaurios carnívoros voladores
    public Flux<CarnivoroVolador> obtenerCarnivorosVoladores() {
        return obtenerDinosauriosPorTipo(CarnivoroVolador.class);
    }

    // Metodo para obtener todos los dinosaurios carnívoros acuáticos
    public Flux<CarnivoroAcuatico> obtenerCarnivorosAcuaticos() {
        return obtenerDinosauriosPorTipo(CarnivoroAcuatico.class);
    }

    // Metodo para obtener todos los dinosaurios herbívoros terrestres
    public Flux<HerbivoroTerrestre> obtenerHerbivorosTerrestres() {
        return obtenerDinosauriosPorTipo(HerbivoroTerrestre.class);
    }

    // Metodo para obtener todos los dinosaurios herbívoros voladores
    public Flux<HerbivoroVolador> obtenerHerbivorosVoladores() {
        return obtenerDinosauriosPorTipo(HerbivoroVolador.class);
    }

    // Metodo para obtener todos los dinosaurios herbívoros acuáticos
    public Flux<HerbivoroAcuatico> obtenerHerbivorosAcuaticos() {
        return obtenerDinosauriosPorTipo(HerbivoroAcuatico.class);
    }

    // Metodo para obtener todos los dinosaurios omnívoros terrestres
    public Flux<OmnivoroTerrestre> obtenerOmnivorosTerrestres() {
        return obtenerDinosauriosPorTipo(OmnivoroTerrestre.class);
    }

    // Metodo para obtener todos los dinosaurios omnívoros voladores
    public Flux<OmnivoroVolador> obtenerOmnivorosVoladores() {
        return obtenerDinosauriosPorTipo(OmnivoroVolador.class);
    }

    // Metodo para obtener todos los dinosaurios omnívoros acuáticos
    public Flux<OmnivoroAcuatico> obtenerOmnivorosAcuaticos() {
        return obtenerDinosauriosPorTipo(OmnivoroAcuatico.class);
    }

    // Metodo para obtener un dinosaurio por su ID de forma completamente reactiva
    public Mono<DinosaurioDTO> getById(String id) {
        return dinosaurioRepository.findById(id)
                .subscribeOn(Schedulers.fromExecutor(executorServiceGetById))
                .flatMap(this::mapToDTO) // Mapea a DTO
                .doOnSuccess(dto -> rabbitMQProducer.enviarMensaje("dinosaurios", "Consultado dinosaurio con ID: " + dto.getId()));
    }

    // Metodo para crear un dinosaurio y comenzar a simular su crecimiento
    public Mono<DinosaurioDTO> create(DinosaurioDTO dto) {
        return mapToEntity(dto)
                .flatMap(dino -> dinosaurioRepository.save(dino)
                        .subscribeOn(Schedulers.fromExecutor(executorServiceCreate))
                        .doOnSuccess(savedDino -> {
                            rabbitMQProducer.enviarMensaje("dinosaurios", "Nuevo dinosaurio creado: " + savedDino.getNombre());
                            iniciarSimulacionCrecimiento(savedDino); // Iniciar el crecimiento del dinosaurio

                            // Obtener la isla en la que se encuentra el dinosaurio y la enfermería
                            islaService.getAll()
                                    .filter(isla -> isla.getDinosaurios() != null && isla.getDinosaurios().stream()
                                            .anyMatch(d -> d.getId().equals(savedDino.getId())))
                                    .next() // Toma la primera isla que contiene al dinosaurio
                                    .flatMap(origenIsla -> {
                                        // Verifica si existe una isla de tipo Enfermeria
                                        return islaService.getAll()
                                                .filter(isla -> isla instanceof Enfermeria)
                                                .cast(Enfermeria.class)
                                                .next()
                                                .switchIfEmpty(islaService.create(new EnfermeriaDTO(
                                                                "enfermeria_id",
                                                                "Enfermería",
                                                                20, // Capacidad de la enfermería
                                                                10, // Tamaño del tablero
                                                                new int[10][10], // Tablero de la enfermería
                                                                new ArrayList<>()
                                                        )).flatMap(islaService::mapToEntity)
                                                        .cast(Enfermeria.class)) // Crear nueva enfermería si no existe
                                                .flatMap(enfermeria -> {
                                                    // Iniciar monitoreo en la enfermería
                                                    return iniciarMonitoreoEnfermeriaDinosaurios(
                                                            islaService.mapToDTO(origenIsla).block(), enfermeria);
                                                });
                                    })
                                    .subscribe();
                        })
                        .flatMap(this::mapToDTO)
                );
    }


    public void iniciarSimulacionCrecimiento(Dinosaurio dino) {
        Flux.interval(Duration.ofMinutes(2)) // Cada 2 minutos representa un mes
                .flatMap(tick -> {
                    // Incrementa la edad y verifica la probabilidad de muerte
                    Mono<Void> incrementoEdadYVerificacionMuerte = Mono.fromRunnable(() -> {
                        lock.lock();
                        try {
                            dino.setEdad(dino.getEdad() + 1); // Incrementa la edad en un mes
                            System.out.println(dino.getNombre() + " ha crecido. Edad actual: " + dino.getEdad() / 12 + " año/s.");

                            // Verificar si el dinosaurio tiene más de 20 años para aplicar la probabilidad de muerte
                            if (dino.getEdad() >= 240) { // 240 meses = 20 años
                                int edadEnAnios = dino.getEdad() / 12;
                                double probabilidadMuerte = 0.01 + (edadEnAnios - 20) * 0.02;
                                Random random = new Random();

                                // Si el dinosaurio muere, se elimina de la isla y de la base de datos
                                if (random.nextDouble() < probabilidadMuerte) {
                                    System.out.println(dino.getNombre() + " ha muerto a la edad de " + edadEnAnios + " años.");
                                    eliminarDinosaurioDeIslaYBdd(dino).subscribe();
                                }
                            }
                        } finally {
                            lock.unlock();
                        }
                    }).then();

                    // Obtener la isla y realizar 10 movimientos y 5 alimentaciones
                    return incrementoEdadYVerificacionMuerte
                            .then(islaService.getAll()
                                    .filter(isla -> isla.getDinosaurios() != null && isla.getDinosaurios().stream()
                                            .anyMatch(d -> d.getId().equals(dino.getId()))) // Encontrar la isla que contiene al dinosaurio
                                    .next()
                                    .flatMap(isla -> islaService.mapToDTO(isla)
                                            .flatMap(islaDTO -> {
                                                // Realizar 10 movimientos
                                                Mono<Void> movimientos = Flux.range(1, 10)
                                                        .flatMap(i -> islaService.iniciarSimulacionMovimiento(islaDTO))
                                                        .then();

                                                // Alimentar al dinosaurio cinco veces
                                                Mono<Void> alimentacion = Flux.range(1, 5)
                                                        .flatMap(i -> alimentarDinosaurio(dino, isla))
                                                        .then();

                                                // Manejar crecimiento, movimiento y alimentación en paralelo
                                                return Mono.when(movimientos, alimentacion)
                                                        .then(Mono.defer(() -> {
                                                            if (dino.estaMaduro()) {
                                                                return obtenerCriaderoParaDinosaurio(dino)
                                                                        .flatMap(criadero -> moverDinoMaduroACriadero(dino, criadero))
                                                                        .then(dinosaurioRepository.save(dino));
                                                            }
                                                            return dinosaurioRepository.save(dino);
                                                        }));
                                            })))
                            .switchIfEmpty(Mono.error(new IllegalArgumentException("El dinosaurio no se encuentra en ninguna isla.")));
                })
                .subscribe();
    }

    private Mono<Void> eliminarDinosaurioDeIslaYBdd(Dinosaurio dino) {
        return islaService.getAll() // Obtiene todas las islas
                .filter(isla -> isla.getDinosaurios() != null && isla.getDinosaurios().stream()
                        .anyMatch(d -> d.getId().equals(dino.getId()))) // Filtra para encontrar la isla que contiene al dinosaurio
                .next() // Toma la primera isla que cumple la condición
                .flatMap(isla ->
                        // Convierte la isla a IslaDTO antes de llamar a eliminarDinosaurioIsla
                        islaService.mapToDTO(isla)
                                .flatMap(islaDTO ->
                                        islaService.eliminarDinosaurioIsla(islaDTO, dino.getId()) // Elimina el dinosaurio de la isla
                                                .then(dinosaurioRepository.deleteById(dino.getId())) // Elimina el dinosaurio de la base de datos
                                                .doOnSuccess(unused -> System.out.println(dino.getNombre() + " ha sido eliminado del sistema."))
                                )
                )
                .switchIfEmpty(Mono.error(new IllegalArgumentException("El dinosaurio no se encuentra en ninguna isla.")));
    }

    private Mono<Criadero> obtenerCriaderoParaDinosaurio(Dinosaurio dino) {
        // Busca en todas las islas para encontrar la que contenga al dinosaurio
        return islaRepository.findAll() // Obtiene todas las islas desde el repositorio
                .filter(isla -> {
                    // Verifica si la isla es un criadero y contiene al dinosaurio
                    if (isla instanceof Criadero) {
                        return isla.getDinosaurios().stream()
                                .anyMatch(dinosaurio -> dinosaurio.getId().equals(dino.getId()));
                    }
                    return false;
                })
                .next() // Obtiene el primer resultado que cumple la condición
                .cast(Criadero.class) // Convierte el resultado a Criadero
                .switchIfEmpty(Mono.error(new IllegalArgumentException("El dinosaurio no se encuentra en un criadero.")));
    }


    // Metodo para actualizar un dinosaurio
    public Mono<DinosaurioDTO> update(String id, DinosaurioDTO dto) {
        return dinosaurioRepository.findById(id)
                .flatMap(existingDino -> mapToEntity(dto)
                        .map(updatedDino -> {
                            existingDino.setNombre(updatedDino.getNombre());
                            existingDino.setEdad(updatedDino.getEdad());
                            existingDino.setHabitat(updatedDino.getHabitat());
                            existingDino.setSensores(updatedDino.getSensores());
                            existingDino.setPosicion(updatedDino.getPosicion());
                            return existingDino;
                        })
                )
                .flatMap(dinosaurioRepository::save)
                .flatMap(this::mapToDTO)
                .doOnSuccess(updatedDto -> rabbitMQProducer.enviarMensaje("dinosaurios", "Dinosaurio actualizado: " + updatedDto.getNombre()))
                .subscribeOn(Schedulers.fromExecutor(executorServiceUpdate));
    }

    // metoddo para enviar una alerta crítica sobre un dinosaurio.
    // Envía un mensaje de alerta a través de RabbitMQ usando la cola específica.*
    public Mono<Void> enviarAlerta(String mensaje) {// Mensaje detallado de alerta para envío a RabbitMQ
        String alerta = "ALERTA CRÍTICA: " + mensaje;

        // Enviar el mensaje a través de RabbitMQ y manejar presión de flujo
        return rabbitMQProducer.aplicarBackpressureOnBuffer("alertas", alerta)
                .doOnSubscribe(subscription -> System.out.println("Enviando alerta a RabbitMQ: " + alerta))
                .doOnError(error -> System.err.println("Error al enviar alerta: " + error.getMessage()))
                .doOnSuccess(aVoid -> System.out.println("Alerta enviada correctamente"))
                .then();
    }


    // Metodo para eliminar un dinosaurio (ahora regresa Mono<Void>)
    public Mono<Void> delete(String id) {
        return dinosaurioRepository.findById(id)
                .flatMap(dino -> dinosaurioRepository.deleteById(id)
                        .doOnSuccess(v -> rabbitMQProducer.enviarMensaje("dinosaurios", "Dinosaurio eliminado con ID: " + id))
                )
                .subscribeOn(Schedulers.fromExecutor(executorServiceDelete))
                .then();
    }



    // metodo para mapear de entidad a DTO
    public Mono<DinosaurioDTO> mapToDTO(Dinosaurio dinosaurio) {
        return Mono.fromCallable(() -> {
            DinosaurioDTO dto;
            if (dinosaurio instanceof CarnivoroTerrestre) {
                dto = new CarnivoroTerrestreDTO();
            } else if (dinosaurio instanceof CarnivoroAcuatico) {
                dto = new CarnivoroAcuaticoDTO();
            } else if (dinosaurio instanceof CarnivoroVolador) {
                dto = new CarnivoroVoladorDTO();
            } else if (dinosaurio instanceof HerbivoroTerrestre) {
                dto = new HerbivoroTerrestreDTO();
            } else if (dinosaurio instanceof HerbivoroAcuatico) {
                dto = new HerbivoroAcuaticoDTO();
            } else if (dinosaurio instanceof HerbivoroVolador) {
                dto = new HerbivoroVoladorDTO();
            } else if (dinosaurio instanceof OmnivoroTerrestre) {
                dto = new OmnivoroTerrestreDTO();
            } else if (dinosaurio instanceof OmnivoroAcuatico) {
                dto = new OmnivoroAcuaticoDTO();
            } else if (dinosaurio instanceof OmnivoroVolador) {
                dto = new OmnivoroVoladorDTO();
            } else {
                throw new IllegalArgumentException("Unknown dinosaur type");
            }
            dto.setId(dinosaurio.getId());
            dto.setNombre(dinosaurio.getNombre());
            dto.setEdad(dinosaurio.getEdad());
            dto.setHabitat(dinosaurio.getHabitat());
            dto.setPosicion(dinosaurio.getPosicion() != null ? new PosicionDTO(dinosaurio.getPosicion().getX(), dinosaurio.getPosicion().getY(), dinosaurio.getPosicion().getZona()) : null);
            dto.setIslaId(dinosaurio.getIslaId());
            dto.setSensores(dinosaurio.getSensores());
            return dto;
        }).subscribeOn(Schedulers.boundedElastic());
    }

    // metodo para mapear de DTO a entidad
    public Mono<Dinosaurio> mapToEntity(DinosaurioDTO dto) {
        return Mono.fromCallable(() -> {
            Dinosaurio dinosaurio = DinosaurioFactory.crearDinosaurio(dto.getNombre(), dto.getHabitat());
            dinosaurio.setNombre(dto.getNombre());
            dinosaurio.setHabitat(dto.getHabitat());
            dinosaurio.setId(dto.getId());
            dinosaurio.setEdad(dto.getEdad());
            dinosaurio.setPosicion(dto.getPosicion() != null ? new Posicion(dto.getPosicion().getX(), dto.getPosicion().getY(), dto.getPosicion().getZona()) : null);
            dinosaurio.setIslaId(dto.getIslaId());
            dinosaurio.setSensores(dto.getSensores());
            return dinosaurio;
        }).subscribeOn(Schedulers.boundedElastic());
    }

    public Mono<Void> alimentarDinosaurio(Dinosaurio dino, Isla isla) {
        return islaRepository.findById(isla.getId())
                .flatMapMany(islaEncontrada -> Flux.fromIterable(islaEncontrada.getDinosaurios()))
                .filter(presa -> !presa.getId().equals(dino.getId()))
                .next()
                .flatMap(presa -> {
                    boolean comer = false;
                    boolean comerCarne;
                    Mono<Void> eliminarPresaMono = Mono.empty();

                    if (dino instanceof Carnivoro) {
                        comer = ((Carnivoro) dino).cazar(presa);
                        if (comer) {
                            eliminarPresaMono = eliminarDinosaurioPorTipo(isla, presa.getId());
                        }
                    } else if (dino instanceof Omnivoro) {
                        comerCarne = ((Omnivoro) dino).buscarComida(presa);
                        if (comerCarne) {
                            eliminarPresaMono = eliminarDinosaurioPorTipo(isla, presa.getId());
                        }
                        comer = comerCarne;
                    } else if (dino instanceof Herbivoro) {
                        dino.comer();
                        comer = true;
                    }

                    if (comer) {
                        System.out.println(dino.getNombre() + " ha comido.");
                    } else {
                        System.out.println(dino.getNombre() + " no ha podido comer.");
                    }

                    return eliminarPresaMono;
                })
                .then();
    }

    // Metodo auxiliar para eliminar dinosaurio usando el tipo de isla
    private Mono<Void> eliminarDinosaurioPorTipo(Isla isla, String dinosaurioId) {
        // Convierte la isla a su correspondiente DTO
        IslaDTO islaDTO = convertirIslaAIslaDTO(isla);

        // Llama al metodo de islaService para eliminar el dinosaurio usando el DTO
        return islaService.eliminarDinosaurioIsla(islaDTO, dinosaurioId);
    }

    // Metodo para convertir una instancia de Isla a su correspondiente DTO
    public IslaDTO convertirIslaAIslaDTO(Isla isla) {
        IslaDTO islaDTO;

        if (isla instanceof IslaAcuatica acuatico) {
            islaDTO = new IslaAcuaticaDTO(
                    acuatico.getId(),
                    acuatico.getNombre(),
                    acuatico.getCapacidadMaxima(),
                    acuatico.getTamanioTablero(),
                    acuatico.getTablero(),
                    acuatico.getDinosaurios()
            );
        } else if (isla instanceof IslaTerrestreAerea terrestreAerea) {
            islaDTO = new IslaTerrestreAereaDTO(
                    terrestreAerea.getId(),
                    terrestreAerea.getNombre(),
                    terrestreAerea.getCapacidadMaxima(),
                    terrestreAerea.getTamanioTablero(),
                    terrestreAerea.getTablero(),
                    terrestreAerea.getDinosaurios()
            );
        } else if (isla instanceof Enfermeria enfermeria) {
            islaDTO = new EnfermeriaDTO(
                    enfermeria.getId(),
                    enfermeria.getNombre(),
                    enfermeria.getCapacidadMaxima(),
                    enfermeria.getTamanioTablero(),
                    enfermeria.getTablero(),
                    enfermeria.getDinosaurios()
            );
        } else if (isla instanceof CriaderoAcuatico criaderoAcuatico) {
            islaDTO = new CriaderoAcuaticoDTO(
                    criaderoAcuatico.getId(),
                    criaderoAcuatico.getNombre(),
                    criaderoAcuatico.getCapacidadMaxima(),
                    criaderoAcuatico.getTamanioTablero(),
                    criaderoAcuatico.getTablero(),
                    criaderoAcuatico.getDinosaurios()
            );
        } else if (isla instanceof CriaderoTerrestre criaderoTerrestre) {
            islaDTO = new CriaderoTerrestreDTO(
                    criaderoTerrestre.getId(),
                    criaderoTerrestre.getNombre(),
                    criaderoTerrestre.getCapacidadMaxima(),
                    criaderoTerrestre.getTamanioTablero(),
                    criaderoTerrestre.getTablero(),
                    criaderoTerrestre.getDinosaurios()
            );
        } else if (isla instanceof CriaderoVoladores criaderoVoladores) {
            islaDTO = new CriaderoVoladoresDTO(
                    criaderoVoladores.getId(),
                    criaderoVoladores.getNombre(),
                    criaderoVoladores.getCapacidadMaxima(),
                    criaderoVoladores.getTamanioTablero(),
                    criaderoVoladores.getTablero(),
                    criaderoVoladores.getDinosaurios()
            );
        } else {
            throw new IllegalArgumentException("Tipo de isla no soportado: " + isla.getClass().getSimpleName());
        }

        return islaDTO;
    }


    public Mono<Void> iniciarMonitoreoEnfermeriaDinosaurios(IslaDTO origenDTO, Enfermeria enfermeria) {
        return getAll() // Obtiene todos los dinosaurios como Flux<Dinosaurio>
                .flatMap(dino -> Flux.interval(Duration.ofMinutes(1)) // Establece el intervalo de monitoreo
                        .flatMap(tick -> detectarYMoverSiEnfermo(dino.getId(), origenDTO, enfermeria)) // Verifica la salud y mueve si es necesario
                )
                .then(); // Retorna un Mono<Void> cuando finaliza
    }

    public Mono<Void> detectarYMoverSiEnfermo(String dinosaurioId, IslaDTO origenDTO, Enfermeria enfermeria) {
        return getById(dinosaurioId)
                .flatMap(this::mapToEntity)
                .flatMap(dino ->
                        sensorService.generarValoresAleatoriosParaSensoresTempCard(dino)
                                .flatMap(valoresSensores -> {
                                    double valorTemperatura = valoresSensores.getOrDefault("temperatura", 37.5); // Valor predeterminado si no existe
                                    double valorFrecuenciaCardiaca = valoresSensores.getOrDefault("frecuenciaCardiaca", 80.0); // Valor predeterminado si no existe

                                    if (dino.estaEnfermo(valorTemperatura, valorFrecuenciaCardiaca)) {
                                        System.out.println(dino.getNombre() + " está enfermo. Enviando alerta y moviéndolo a la enfermería.");

                                        // Enviar una alerta antes de mover al dinosaurio a la enfermería
                                        return enviarAlerta("El dinosaurio " + dino.getNombre() + " está enfermo y necesita atención médica.")
                                                .then(islaService.getById(enfermeria.getId())
                                                        .flatMap(enfermeriaIsla -> {
                                                            int dinosaurCount = enfermeriaIsla.getDinosaurios() != null ? enfermeriaIsla.getDinosaurios().size() : 0;
                                                            if (dinosaurCount >= enfermeriaIsla.getCapacidadMaxima()) {
                                                                System.out.println("La enfermería está llena. Reintentando en 1 minuto.");
                                                                return Mono.delay(Duration.ofMinutes(1))
                                                                        .then(detectarYMoverSiEnfermo(dinosaurioId, origenDTO, enfermeria));
                                                            } else {
                                                                return islaService.moverDinosaurioIsla(dino.getId(), origenDTO,
                                                                        new EnfermeriaDTO(
                                                                                enfermeria.getId(),
                                                                                enfermeria.getNombre(),
                                                                                enfermeria.getCapacidadMaxima(),
                                                                                enfermeria.getTamanioTablero(),
                                                                                enfermeria.getTablero(),
                                                                                enfermeria.getDinosaurios()
                                                                        )
                                                                ).then(iniciarSimulacionRecuperacion(dino, origenDTO));
                                                            }
                                                        }));
                                    }
                                    return Mono.empty();
                                })
                );
    }

    public Mono<Void> iniciarSimulacionRecuperacion(Dinosaurio dino, IslaDTO origenDTO) {
        return Mono.fromRunnable(() -> {
            Executors.newSingleThreadScheduledExecutor().schedule(() -> {
                System.out.println(dino.getNombre() + " se ha recuperado y está listo para regresar a su isla de origen.");

                // Obtener la enfermería y mostrar su tablero
                islaService.getAll()
                        .filter(isla -> isla instanceof Enfermeria)
                        .next()
                        .flatMap(enfermeria -> {
                            System.out.println("Tablero de enfermería antes de la eliminación:");
                            printTablero(enfermeria.getTablero());
                            return islaService.mapToDTO(enfermeria);
                        })
                        .flatMap(enfermeriaDTO -> {
                            // Eliminar el dinosaurio de la enfermería antes de moverlo a la isla de origen
                            return islaService.eliminarDinosaurioIsla(enfermeriaDTO, dino.getId())
                                    .then(islaService.mapToEntity(origenDTO)) // Convertir origenDTO a la entidad Isla
                                    .flatMap(islaOriginal -> {
                                        // Comprobar si la posición original está libre en la isla de origen
                                        Posicion posicionAnterior = dino.getPosicion();
                                        if (islaOriginal.esPosicionValida(posicionAnterior) &&
                                                islaOriginal.getTablero()[posicionAnterior.getX()][posicionAnterior.getY()] == 0) {
                                            // La posición original está libre; reinsertar al dinosaurio en esa posición
                                            return islaOriginal.agregarDinosaurio(dino, posicionAnterior)
                                                    .then(islaRepository.save(islaOriginal))
                                                    .doOnSuccess(savedIsla -> {
                                                        System.out.println("Tablero de la isla de origen después de la reubicación:");
                                                        printTablero(savedIsla.getTablero());
                                                    }).then(); // Guarda el estado actualizado de la isla
                                        } else {
                                            // La posición está ocupada; encontrar una nueva posición disponible
                                            Posicion nuevaPosicion = encontrarNuevaPosicionDisponible(islaOriginal);
                                            if (nuevaPosicion != null) {
                                                return islaOriginal.agregarDinosaurio(dino, nuevaPosicion)
                                                        .then(islaRepository.save(islaOriginal))
                                                        .doOnSuccess(savedIsla -> {
                                                            System.out.println("Tablero de la isla de origen después de la reubicación:");
                                                            printTablero(savedIsla.getTablero());
                                                        }).then(); // Guarda el estado actualizado de la isla
                                            } else {
                                                return Mono.error(new IllegalStateException("No hay posiciones disponibles en la isla de origen para mover el dinosaurio."));
                                            }
                                        }
                                    });
                        })
                        .doOnError(error -> System.err.println("Error en la reubicación del dinosaurio: " + error.getMessage()))
                        .subscribe();
            }, 10, TimeUnit.SECONDS); // Simula la recuperación con un retraso de 10 segundos
        }).then();
    }

    // Metodo auxiliar para imprimir el tablero de la isla o enfermería
    private void printTablero(int[][] tablero) {
        for (int[] fila : tablero) {
            for (int celda : fila) {
                System.out.print(celda + " ");
            }
            System.out.println();
        }
    }

    // Metodo auxiliar para encontrar una nueva posición disponible en la isla
    private Posicion encontrarNuevaPosicionDisponible(Isla isla) {
        for (int x = 0; x < isla.getTamanioTablero(); x++) {
            for (int y = 0; y < isla.getTamanioTablero(); y++) {
                if (isla.getTablero()[x][y] == 0) { // Verifica si la posición está libre
                    return new Posicion(x, y, "zona_predeterminada"); // Ajusta la zona según tus necesidades
                }
            }
        }
        return null; // No se encontró ninguna posición disponible
    }

    public Mono<Void> moverDinoMaduroACriadero(Dinosaurio dino, Criadero criadero) {
        return Mono.just(dino)
                .filter(Dinosaurio::estaMaduro) // Verifica si el dinosaurio ha alcanzado la madurez
                .flatMap(maduroDino -> {
                    System.out.println("Moviendo " + maduroDino.getNombre() + " del criadero a su isla correspondiente.");

                    Mono<Isla> destinoIslaMono; // Cambiado para usar Isla directamente si se espera Isla en el metodo final
                    CriaderoDTO criaderoDTO;

                    // Determina la isla de destino según el tipo de dinosaurio
                    if (maduroDino instanceof CarnivoroAcuatico || maduroDino instanceof HerbivoroAcuatico || maduroDino instanceof OmnivoroAcuatico) {
                        destinoIslaMono = obtenerDestinoIslaOReintentar(IslaAcuatica.class, new IslaAcuaticaDTO(
                                "isla_acuatica_id",
                                "Isla Acuática",
                                30, // Capacidad de la isla
                                15, // Tamaño del tablero
                                new int[15][15], // Tablero de ejemplo
                                null // Lista inicial de dinosaurios
                        ));

                        criaderoDTO = new CriaderoAcuaticoDTO(
                                criadero.getId(),
                                criadero.getNombre(),
                                criadero.getCapacidadMaxima(),
                                criadero.getTamanioTablero(),
                                criadero.getTablero(),
                                criadero.getDinosaurios()
                        );

                    } else if (maduroDino instanceof CarnivoroVolador || maduroDino instanceof HerbivoroVolador || maduroDino instanceof OmnivoroVolador) {
                        destinoIslaMono = obtenerDestinoIslaOReintentar(IslaTerrestreAerea.class, new IslaTerrestreAereaDTO(
                                "isla_voladora_id",
                                "Isla Terrestre-Aérea",
                                40,
                                20,
                                new int[20][20],
                                null
                        ));

                        criaderoDTO = new CriaderoVoladoresDTO(
                                criadero.getId(),
                                criadero.getNombre(),
                                criadero.getCapacidadMaxima(),
                                criadero.getTamanioTablero(),
                                criadero.getTablero(),
                                criadero.getDinosaurios()
                        );

                    } else {
                        destinoIslaMono = obtenerDestinoIslaOReintentar(IslaTerrestreAerea.class, new IslaTerrestreAereaDTO(
                                "isla_terrestre_id",
                                "Isla Terrestre",
                                50,
                                25,
                                new int[25][25],
                                null
                        ));

                        criaderoDTO = new CriaderoTerrestreDTO(
                                criadero.getId(),
                                criadero.getNombre(),
                                criadero.getCapacidadMaxima(),
                                criadero.getTamanioTablero(),
                                criadero.getTablero(),
                                criadero.getDinosaurios()
                        );
                    }

                    // Mueve el dinosaurio desde el criadero específico a la isla de destino
                    return destinoIslaMono
                            .flatMap(destinoIsla -> islaService.mapToDTO(destinoIsla)
                                    .flatMap(destinoIslaDTO ->
                                            islaService.moverDinosaurioIsla(
                                                    maduroDino.getId(),
                                                    criaderoDTO,
                                                    destinoIslaDTO
                                            )
                                    ));

                }).then();
    }

    // Este metodo intenta obtener una isla de destino disponible o crear una nueva si no hay ninguna.
// Si encuentra una isla llena, espera un minuto y vuelve a intentarlo.
    private <T extends Isla> Mono<Isla> obtenerDestinoIslaOReintentar(Class<T> islaClase, IslaDTO nuevaIslaDTO) {
        return islaService.getAll()
                .filter(isla -> islaClase.isInstance(isla) && isla.getDinosaurios().size() < isla.getCapacidadMaxima())
                .next()
                .switchIfEmpty(Mono.defer(() -> {
                    System.out.println("No se encontró una isla disponible o la isla está llena. Esperando 1 minuto para reintentar...");
                    return Mono.delay(Duration.ofMinutes(1))
                            .then(obtenerDestinoIslaOReintentar(islaClase, nuevaIslaDTO));
                }))
                .switchIfEmpty(islaService.create(nuevaIslaDTO).flatMap(islaService::mapToEntity));
    }

}

