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
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
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

    // Set para almacenar los dinosaurios en traslado
    private final Set<String> dinosauriosEnTraslado = ConcurrentHashMap.newKeySet();

    // Bloqueo para sincronización de tareas críticas
    private final ReentrantLock lock = new ReentrantLock(); // Bloqueo para sincronización de tareas críticas

    // Mapa de tokens de cancelación para cada isla
    private final Map<String, AtomicBoolean> simulacionCancelTokens = new ConcurrentHashMap<>();

    private final Semaphore accesoEnfermeria = new Semaphore(1);

    private final Map<String, AtomicBoolean> cancelTokensCrecimiento = new ConcurrentHashMap<>();



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

    //Instancia de simulacionCancelTokens
    public void iniciarSimulacionCancelTokens(String islaId, AtomicBoolean cancelToken) {
        simulacionCancelTokens.put(islaId, cancelToken);
    }
    public void cancelarSimulacion(String islaId) {
        simulacionCancelTokens.remove(islaId);
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

    public Mono<DinosaurioDTO> create(DinosaurioDTO dto) {
        return mapToEntity(dto)
                .flatMap(dino -> dinosaurioRepository.save(dino)
                        .subscribeOn(Schedulers.fromExecutor(executorServiceCreate))
                        .doOnSuccess(savedDino -> {
                            rabbitMQProducer.enviarMensaje("dinosaurios", "Nuevo dinosaurio creado: " + savedDino.getNombre());
                            iniciarSimulacionCrecimiento(savedDino); // Iniciar el crecimiento del dinosaurio

                            // Verificar si existe una isla de tipo Enfermeria o crearla si no existe
                            islaService.getAll()
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
                                            .cast(Enfermeria.class))
                                    .flatMap(enfermeria -> {
                                        // Iniciar el monitoreo con referencia dinámica a la isla de origen actualizada
                                        return iniciarMonitoreoEnfermeriaDinosaurios(savedDino, enfermeria);
                                    })
                                    .subscribe();
                        })
                        .flatMap(this::mapToDTO)
                );
    }

    public void iniciarSimulacionCrecimiento(Dinosaurio dino) {
        // Crear o recuperar el token de cancelación para este dinosaurio
        AtomicBoolean cancelToken = cancelTokensCrecimiento.computeIfAbsent(dino.getId(), id -> new AtomicBoolean(false));

        Flux.interval(Duration.ofMinutes(2)) // Cada 2 minutos representa un mes
                .takeWhile(tick -> !cancelToken.get()) // Detiene la simulación si el token se activa
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
                                    eliminarDinosaurioDeIslaYBdd(dino)
                                            .doOnTerminate(() -> cancelToken.set(true)) // Activa el token de cancelación al eliminar el dinosaurio
                                            .subscribe();
                                }
                            }
                        } finally {
                            lock.unlock();
                        }
                    }).then();

                    // Obtener la isla y realizar movimientos y alimentaciones solo si el dinosaurio no está en la enfermería
                    return incrementoEdadYVerificacionMuerte
                            .then(islaService.getAll()
                                    .filter(isla -> isla.getDinosaurios() != null && isla.getDinosaurios().stream()
                                            .anyMatch(d -> d.getId().equals(dino.getId()))) // Encontrar la isla que contiene al dinosaurio
                                    .next()
                                    .flatMap(isla -> islaService.mapToDTO(isla)
                                            .flatMap(islaDTO -> {
                                                if (isla instanceof Enfermeria) {
                                                    System.out.println(dino.getNombre() + " está en la enfermería. No se realizarán movimientos ni alimentaciones.");
                                                    return Mono.empty();
                                                }

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
                            .switchIfEmpty(Mono.error(new IllegalArgumentException("El dinosaurio no se encuentra en ninguna isla.")))
                            .onErrorResume(e -> {
                                System.err.println("Error encontrado: " + e.getMessage());
                                return Mono.empty(); // Continúa el flujo sin detenerlo
                            });
                })
                .doFinally(signal -> {
                    // Remueve el token de cancelación para limpiar el recurso al finalizar
                    cancelTokensCrecimiento.remove(dino.getId());
                    System.out.println("Simulación de crecimiento detenida para " + dino.getNombre());
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
                                        eliminarDinosaurioPorTipo(isla, dino.getId())
                                                .then(delete(dino.getId()))
                                                .doOnSuccess(unused -> System.out.println(dino.getNombre() + " ha sido eliminado del sistema."))
                                )
                )
                .switchIfEmpty(Mono.error(new IllegalArgumentException("El dinosaurio no se encuentra en ninguna isla.")));
    }

    private Mono<Criadero> obtenerCriaderoParaDinosaurio(Dinosaurio dino) {
        // Busca en todas las islas para encontrar la que contenga al dinosaurio
        return islaRepository.findAll()
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
                .switchIfEmpty( // En caso de que el dinosaurio no esté en un criadero, verifica si está en la enfermería
                        islaRepository.findAll()
                                .filter(isla -> isla instanceof Enfermeria)
                                .filter(isla -> isla.getDinosaurios().stream()
                                        .anyMatch(dinosaurio -> dinosaurio.getId().equals(dino.getId())))
                                .next()
                                .flatMap(enfermeria -> {
                                    System.out.println("El dinosaurio " + dino.getNombre() + " está en la enfermería. Esperando para reintentar...");
                                    return Mono.delay(Duration.ofSeconds(10)) // Espera un minuto antes de reintentar
                                            .then(obtenerCriaderoParaDinosaurio(dino)); // Reintenta la operación
                                })
                                .switchIfEmpty(Mono.error(new IllegalArgumentException("El dinosaurio no se encuentra en un criadero o en la enfermería.")))
                );
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

    private Mono<Void> eliminarDinosaurioPorTipo(Isla isla, String dinosaurioId) {
        return Mono.justOrEmpty(isla.getDinosaurios().stream()
                        .filter(dino -> dino.getId().equals(dinosaurioId))
                        .findFirst())
                .flatMap(dinoAEliminar -> {
                    // Activar el token de cancelación de crecimiento
                    AtomicBoolean cancelToken = cancelTokensCrecimiento.get(dinosaurioId);
                    if (cancelToken != null) {
                        cancelToken.set(true);
                    }

                    // Eliminar del tablero
                    Posicion posicion = dinoAEliminar.getPosicion();
                    if (posicion != null && islaService.esPosicionValida(isla, posicion)) {
                        isla.getTablero()[posicion.getX()][posicion.getY()] = 0;
                    }

                    // Eliminar de la lista de dinosaurios en la isla
                    isla.getDinosaurios().remove(dinoAEliminar);

                    // Eliminar de la base de datos
                    return dinosaurioRepository.delete(dinoAEliminar)
                            .then(islaRepository.save(isla))
                            .then(Mono.fromRunnable(() -> {
                                System.out.println("Dinosaurio " + dinoAEliminar.getNombre() + " eliminado del tablero y de la base de datos.");
                            }));
                })
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Dinosaurio no encontrado en la isla."))).then();
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


    public Mono<Void> iniciarMonitoreoEnfermeriaDinosaurios(Dinosaurio dino, Enfermeria enfermeria) {
        return Flux.interval(Duration.ofMinutes(1))
                .flatMap(tick -> {
                    // Control de acceso para que solo un dinosaurio sea revisado a la vez
                    return Mono.fromRunnable(() -> {
                        try {
                            accesoEnfermeria.acquire();
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                            throw new RuntimeException("Error al intentar adquirir acceso a la enfermería", e);
                        }
                    }).then(
                            // Obtener la isla actual del dinosaurio y verificar si está enfermo
                            islaService.getAll()
                                    .filter(isla -> isla.getDinosaurios() != null && isla.getDinosaurios().stream()
                                            .anyMatch(d -> d.getId().equals(dino.getId())))
                                    .next()
                                    .flatMap(origenIsla -> detectarYMoverSiEnfermo(dino.getId(), islaService.mapToDTO(origenIsla).block(), enfermeria))
                    ).doFinally(signal -> accesoEnfermeria.release()); // Liberar el acceso
                })
                .then();
    }

    public Mono<Void> detectarYMoverSiEnfermo(String dinosaurioId, IslaDTO origenDTO, Enfermeria enfermeria) {
        return getById(dinosaurioId)
                .flatMap(this::mapToEntity)
                .flatMap(dino -> sensorService.generarValoresAleatoriosParaSensoresTempCard(dino)
                        .flatMap(valoresSensores -> {
                            double valorTemperatura = valoresSensores.getOrDefault("temperatura", 37.5);
                            double valorFrecuenciaCardiaca = valoresSensores.getOrDefault("frecuenciaCardiaca", 80.0);

                            if (dino.estaEnfermo(valorTemperatura, valorFrecuenciaCardiaca)) {
                                System.out.println(dino.getNombre() + " está enfermo.");

                                return islaRepository.findById(dino.getIslaId())
                                        .flatMap(islaActual -> {
                                            if (islaActual instanceof Enfermeria) {
                                                System.out.println("El dinosaurio " + dino.getNombre() + " ya está en la enfermería. Cancelando traslado.");
                                                return Mono.empty();
                                            } else {
                                                System.out.println("Moviendo " + dino.getNombre() + " a la enfermería.");

                                                AtomicBoolean cancelToken = simulacionCancelTokens.get(origenDTO.getId());
                                                if (cancelToken != null) {
                                                    cancelToken.set(true);
                                                }

                                                return enviarAlerta("El dinosaurio " + dino.getNombre() + " está enfermo y necesita atención médica.")
                                                        .then(islaRepository.findById(enfermeria.getId())
                                                                .flatMap(enfermeriaIsla -> {
                                                                    int dinosaurCount = enfermeriaIsla.getDinosaurios() != null ? enfermeriaIsla.getDinosaurios().size() : 0;
                                                                    if (dinosaurCount >= enfermeriaIsla.getCapacidadMaxima()) {
                                                                        System.out.println("La enfermería está llena. Reintentando en 10 segundos.");
                                                                        return Mono.delay(Duration.ofSeconds(10))
                                                                                .then(detectarYMoverSiEnfermo(dinosaurioId, origenDTO, enfermeria));
                                                                    } else {
                                                                        // Find a new position in the infirmary
                                                                        Posicion nuevaPosicion = encontrarNuevaPosicionDisponible(enfermeriaIsla);
                                                                        if (nuevaPosicion == null) {
                                                                            return Mono.error(new IllegalStateException("No hay posiciones disponibles en la enfermería."));
                                                                        }

                                                                        // Update dinosaur's position and island ID
                                                                        dino.setPosicion(nuevaPosicion);
                                                                        dino.setIslaId(enfermeria.getId());

                                                                        // Add the dinosaur to the infirmary
                                                                        return enfermeriaIsla.agregarDinosaurio(dino, nuevaPosicion)
                                                                                .then(islaRepository.save(enfermeriaIsla))
                                                                                .then(dinosaurioRepository.save(dino))
                                                                                .then(Mono.defer(() -> {
                                                                                    // Remove the dinosaur from the original island
                                                                                    return islaActual.eliminarDinosaurio(dino)
                                                                                            .then(islaRepository.save(islaActual))
                                                                                            .then(iniciarSimulacionRecuperacion(dino, origenDTO));
                                                                                }));
                                                                    }
                                                                }));
                                            }
                                        });
                            }
                            return Mono.empty();
                        }));
    }

    public Mono<Void> iniciarSimulacionRecuperacion(Dinosaurio dino, IslaDTO origenDTO) {
        if (dinosauriosEnTraslado.contains(dino.getId())) {
            System.out.println("Dinosaurio " + dino.getNombre() + " ya está en proceso de traslado.");
            return Mono.empty();
        }

        dinosauriosEnTraslado.add(dino.getId());

        return Mono.fromRunnable(() -> {
            Executors.newSingleThreadScheduledExecutor().schedule(() -> {
                System.out.println(dino.getNombre() + " se ha recuperado y está listo para regresar a su isla de origen.");

                // Obtener la enfermería y eliminar al dinosaurio de ella
                islaRepository.findById("3")
                        .flatMap(enfermeria -> {
                            System.out.println("Tablero de enfermería antes de la eliminación:");
                            printTablero(enfermeria.getTablero());

                            return enfermeria.eliminarDinosaurio(dino)
                                    .then(islaRepository.save(enfermeria))
                                    .doOnSuccess(savedEnfermeria -> {
                                        System.out.println("Tablero de enfermería después de la eliminación:");
                                        printTablero(savedEnfermeria.getTablero());
                                    })
                                    .doOnError(error -> {
                                        System.err.println("Error eliminando dinosaurio de la enfermería: " + error.getMessage());
                                        dinosauriosEnTraslado.remove(dino.getId());
                                    });
                        })
                        .flatMap(unused -> {
                            return islaRepository.findById(origenDTO.getId())
                                    .flatMap(islaOriginal -> {
                                        Posicion posicionAnterior = dino.getPosicion();

                                        if (islaOriginal.getDinosaurios().stream().anyMatch(d -> d.getId().equals(dino.getId()))) {
                                            System.out.println("El dinosaurio ya está en la isla de origen. Cancelando traslado.");
                                            dinosauriosEnTraslado.remove(dino.getId());
                                            return Mono.empty();
                                        }

                                        if (islaOriginal.esPosicionValida(posicionAnterior) &&
                                                islaOriginal.getTablero()[posicionAnterior.getX()][posicionAnterior.getY()] == 0) {
                                            return islaOriginal.agregarDinosaurio(dino, posicionAnterior)
                                                    .then(islaRepository.save(islaOriginal))
                                                    .doOnSuccess(savedIsla -> {
                                                        System.out.println("Tablero de la isla de origen después de la reubicación:");
                                                        printTablero(savedIsla.getTablero());
                                                        dino.setIslaId(origenDTO.getId());
                                                        dino.setPosicion(posicionAnterior);
                                                    })
                                                    .then(dinosaurioRepository.save(dino))
                                                    .doFinally(signalType -> dinosauriosEnTraslado.remove(dino.getId()));
                                        } else {
                                            Posicion nuevaPosicion = encontrarNuevaPosicionDisponible(islaOriginal);
                                            if (nuevaPosicion != null) {
                                                return islaOriginal.agregarDinosaurio(dino, nuevaPosicion)
                                                        .then(islaRepository.save(islaOriginal))
                                                        .doOnSuccess(savedIsla -> {
                                                            System.out.println("Tablero de la isla de origen después de la reubicación:");
                                                            printTablero(savedIsla.getTablero());
                                                            dino.setIslaId(origenDTO.getId());
                                                            dino.setPosicion(nuevaPosicion);
                                                        })
                                                        .then(dinosaurioRepository.save(dino))
                                                        .doFinally(signalType -> dinosauriosEnTraslado.remove(dino.getId()));
                                            } else {
                                                dinosauriosEnTraslado.remove(dino.getId());
                                                return Mono.error(new IllegalStateException("No hay posiciones disponibles en la isla de origen para mover el dinosaurio."));
                                            }
                                        }
                                    })
                                    .doOnError(error -> System.err.println("Error recuperando la isla de origen: " + error.getMessage()));
                        })
                        .doOnError(error -> dinosauriosEnTraslado.remove(dino.getId()))
                        .subscribe();
            }, 10, TimeUnit.SECONDS);
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

    public Mono<Void> moverDinoMaduroACriadero(Dinosaurio dino, Criadero criadero) {
        // Verifica si el dinosaurio ya está en traslado para evitar duplicaciones
        if (dinosauriosEnTraslado.contains(dino.getId())) {
            System.out.println("El dinosaurio " + dino.getNombre() + " ya está en traslado. Cancelando movimiento.");
            return Mono.empty();
        }

        // Agrega el dinosaurio al conjunto de traslado para bloquear duplicaciones
        dinosauriosEnTraslado.add(dino.getId());

        // Cancela cualquier simulación de movimiento activa para la isla de origen del dinosaurio
        AtomicBoolean cancelToken = simulacionCancelTokens.get(dino.getIslaId());
        if (cancelToken != null) {
            cancelToken.set(true); // Solicita la cancelación
        }

        // Verificar si el dinosaurio está en la enfermería antes de moverlo
        return islaRepository.findById(dino.getIslaId())
                .flatMap(islaActual -> {
                    if (islaActual instanceof Enfermeria) {
                        System.out.println("El dinosaurio " + dino.getNombre() + " está en la enfermería. Esperando para reintentar...");
                        // Espera 1 minuto y vuelve a intentar el traslado
                        return Mono.delay(Duration.ofSeconds(30))
                                .then(moverDinoMaduroACriadero(dino, criadero)); // Reintenta la operación
                    }
                    return Mono.just(islaActual); // Procede si no está en la enfermería
                })
                .flatMap(islaActual -> {
                    return Mono.just(dino)
                            .filter(Dinosaurio::estaMaduro) // Verifica si el dinosaurio ha alcanzado la madurez
                            .flatMap(maduroDino -> {
                                System.out.println("Moviendo " + maduroDino.getNombre() + " del criadero a su isla correspondiente.");

                                Mono<Isla> destinoIslaMono;
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
                                        .flatMap(destinoIsla -> {
                                            // Encontrar una nueva posición en el tablero de destino
                                            Posicion nuevaPosicion = encontrarNuevaPosicionDisponible(destinoIsla);
                                            if (nuevaPosicion == null) {
                                                return Mono.error(new IllegalStateException("No hay posiciones disponibles en la isla de destino."));
                                            }

                                            // Actualizar posición y referencia de isla en el dinosaurio
                                            maduroDino.setPosicion(nuevaPosicion);
                                            maduroDino.setIslaId(destinoIsla.getId());

                                            // Actualizar tablero en la isla de destino y mover el dinosaurio
                                            return destinoIsla.agregarDinosaurio(maduroDino, nuevaPosicion)
                                                    .then(islaService.mapToDTO(destinoIsla)
                                                            .flatMap(destinoIslaDTO -> islaService.moverDinosaurioIsla(
                                                                    maduroDino.getId(),
                                                                    criaderoDTO,
                                                                    destinoIslaDTO
                                                            )));
                                        })
                                        .doFinally(signal -> dinosauriosEnTraslado.remove(dino.getId())); // Libera el bloqueo al finalizar
                            });
                });
    }

    // Metodo auxiliar para encontrar una nueva posición en el tablero
    private Posicion encontrarNuevaPosicionDisponible(Isla isla) {
        for (int x = 0; x < isla.getTamanioTablero(); x++) {
            for (int y = 0; y < isla.getTamanioTablero(); y++) {
                if (isla.getTablero()[x][y] == 0) { // Verifica si la posición está libre
                    return new Posicion(x, y, "zona_predeterminada");
                }
            }
        }
        return null; // No se encontró ninguna posición disponible
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

