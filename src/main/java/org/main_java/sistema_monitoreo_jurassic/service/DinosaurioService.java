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
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.time.Duration;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;


@Service
public class DinosaurioService {

    // Inyectamos el productor de RabbitMQ
    private final RabbitMQProducer rabbitMQProducer;
    // Inyectamos el repositorio de dinosaurios
    private final DinosaurioRepository dinosaurioRepository;
    // Inyectamos el repositorio de islas
    private final IslaRepository islaRepository;
    // Inyectamos el factory de dinosaurios
    private final DinosaurioFactory dinosaurioFactory;
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



    // Inyectamos el repositorio de dinosaurios, el factory de dinosaurios y el productor de RabbitMQ
    @Autowired
    public DinosaurioService(DinosaurioRepository dinosaurioRepository,
                             IslaRepository islaRepository,
                             DinosaurioFactory dinosaurioFactory,
                             RabbitMQProducer rabbitMQProducer,
                             IslaService islaService) {
        this.islaService = islaService;
        this.islaRepository = islaRepository;
        this.dinosaurioRepository = dinosaurioRepository;
        this.dinosaurioFactory = dinosaurioFactory;
        this.rabbitMQProducer = rabbitMQProducer;
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

    // Metodo para crear un dinosaurio (ahora regresa Mono<DinosaurioDTO>)
    public Mono<DinosaurioDTO> create(DinosaurioDTO dto) {
        return mapToEntity(dto)
                .flatMap(dino -> dinosaurioRepository.save(dino)
                        .subscribeOn(Schedulers.fromExecutor(executorServiceCreate))
                        .doOnSuccess(savedDino -> rabbitMQProducer.enviarMensaje("dinosaurios", "Nuevo dinosaurio creado: " + savedDino.getNombre()))
                        .flatMap(this::mapToDTO)
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
            return dto;
        }).subscribeOn(Schedulers.boundedElastic());
    }

    // metodo para mapear de DTO a entidad
    public Mono<Dinosaurio> mapToEntity(DinosaurioDTO dto) {
        return Mono.fromCallable(() -> {
            Dinosaurio dinosaurio = dinosaurioFactory.crearDinosaurio(dto.getNombre(), dto.getHabitat());
            dinosaurio.setId(dto.getId());
            dinosaurio.setEdad(dto.getEdad());
            dinosaurio.setPosicion(dto.getPosicion() != null ? new Posicion(dto.getPosicion().getX(), dto.getPosicion().getY(), dto.getPosicion().getZona()) : null);
            return dinosaurio;
        }).subscribeOn(Schedulers.boundedElastic());
    }

    public Mono<Void> alimentarDinosaurio(Dinosaurio dino, Isla isla) {
        return islaRepository.findById(isla.getId()) // Obtener la isla de la base de datos
                .flatMapMany(islaEncontrada -> Flux.fromIterable(islaEncontrada.getDinosaurios())) // Convertir la lista de dinosaurios a Flux
                .filter(presa -> !presa.getId().equals(dino.getId())) // Excluir al dinosaurio que está buscando comida
                .next() // Obtener el primer dinosaurio que puede servir de presa
                .flatMap(presa -> {
                    if (dino instanceof Carnivoro) {
                        ((Carnivoro) dino).cazar(presa); // Carnívoro caza a la presa
                    } else if (dino instanceof Omnivoro) {
                        ((Omnivoro) dino).buscarComida(presa); // Omnívoro busca comida y puede decidir cazar
                    } else if (dino instanceof Herbivoro) {
                        (dino).comer(); // Herbívoro come plantas
                    }
                    return Mono.empty();
                })
                .then();
    }

    // Metodo para detectar si el dinosaurio está enfermo y moverlo a la enfermería
    public Mono<Void> detectarYMoverSiEnfermo(String dinosaurioId, IslaDTO origenDTO, Enfermeria enfermeria) {
        double valorTemperatura = 37.5; // Ejemplo de valor de temperatura
        double valorMovimiento = 5.0;   // Ejemplo de valor de movimiento

        return getById(dinosaurioId)
                .flatMap(this::mapToEntity)
                .flatMap(dino -> {
                    if (dino.estaEnfermo(valorTemperatura, valorMovimiento)) {
                        System.out.println(dino.getNombre() + " está enfermo. Intentando moverlo a la enfermería.");

                        return islaService.getById(enfermeria.getId())
                                .flatMap(enfermeriaIsla -> {
                                    // Verifica si la capacidad máxima de la enfermería ha sido alcanzada
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
                                        );
                                    }
                                });
                    }
                    return Mono.empty();
                });
    }

    // Metodo para simular recuperación y mover el dinosaurio de la enfermería a la isla original
    public Mono<Void> iniciarSimulacionRecuperacion(Dinosaurio dino, IslaDTO origenDTO) {
        return Mono.fromRunnable(() -> {
            Executors.newSingleThreadScheduledExecutor().schedule(() -> {
                System.out.println(dino.getNombre() + " se ha recuperado y está listo para regresar a su isla de origen.");

                // Verifica si la isla de origen tiene espacio y mueve al dinosaurio
                islaService.getById(origenDTO.getId())
                        .flatMap(islaOriginal -> {
                            int dinosaurCount = islaOriginal.getDinosaurios() != null ? islaOriginal.getDinosaurios().size() : 0;
                            if (dinosaurCount >= islaOriginal.getCapacidadMaxima()) {
                                System.out.println("La isla de origen está llena. Reintentando en 1 minuto.");
                                return Mono.delay(Duration.ofMinutes(1))
                                        .then(iniciarSimulacionRecuperacion(dino, origenDTO));
                            } else {
                                return islaService.moverDinosaurioIsla(dino.getId(),
                                        new EnfermeriaDTO(
                                                "enfermeria_id", // Identificador real de la enfermería
                                                "Enfermería",
                                                20, // Capacidad máxima de la enfermería
                                                10, // Tamaño del tablero de la enfermería
                                                new int[10][10], // Tablero de la enfermería
                                                null
                                        ),
                                        origenDTO);
                            }
                        }).subscribe();
            }, 10, TimeUnit.SECONDS); // Simula la recuperación con un retraso de 10 segundos
        });
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

