package org.main_java.sistema_monitoreo_jurassic.domain.islas;

import lombok.*;
import org.main_java.sistema_monitoreo_jurassic.domain.dinosaurios.Dinosaurio;
import org.main_java.sistema_monitoreo_jurassic.domain.dinosaurios.Posicion;
import org.main_java.sistema_monitoreo_jurassic.repos.DinosaurioRepository;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Document(collection = "islas")
public abstract class Isla {
    @Id
    private String id;
    private String nombre;
    private int capacidadMaxima;
    private int[][] tablero; // Tablero de NXN para la isla
    private int tamanioTablero; // Tamaño del tablero N (para una matriz N x N)
    private List<Dinosaurio> dinosaurios;

    @Transient
    private final ReentrantLock lock = new ReentrantLock();

    public Isla(String id, String nombre, int capacidadMaxima, int tamanioTablero, List<Dinosaurio> dinosaurios) {
        this.id = id;
        this.nombre = nombre;
        this.capacidadMaxima = capacidadMaxima;
        this.tamanioTablero = tamanioTablero;
        this.tablero = new int[tamanioTablero][tamanioTablero];
        this.dinosaurios = dinosaurios != null ? new ArrayList<>(dinosaurios) : new ArrayList<>();
        inicializarTablero();
    }

    // Inicializa el tablero a 0 (vacío)
    public void inicializarTablero() {
        for (int i = 0; i < tamanioTablero; i++) {
            for (int j = 0; j < tamanioTablero; j++) {
                tablero[i][j] = 0;
            }
        }
    }

    public Mono<Void> agregarDinosaurio(Dinosaurio dino, Posicion posicion) {
        return Mono.fromCallable(() -> {
                    lock.lock();
                    try {
                        if (dinosaurios.contains(dino)) {
                            throw new IllegalArgumentException("El dinosaurio ya está en esta isla.");
                        }

                        if (dinosaurios.size() >= capacidadMaxima) {
                            throw new IllegalArgumentException("No se pudo agregar el dinosaurio a la isla " + nombre + ". Capacidad máxima alcanzada.");
                        }

                        if (!esPosicionValida(posicion) || tablero[posicion.getX()][posicion.getY()] != 0) {
                            throw new IllegalArgumentException("No se pudo agregar el dinosaurio a la isla " + nombre + ". Posición ocupada o no válida.");
                        }

                        dinosaurios.add(dino);
                        tablero[posicion.getX()][posicion.getY()] = 1;
                        dino.setPosicion(posicion);
                        dino.setIslaId(this.getId());
                        System.out.println("Dinosaurio agregado a la isla " + nombre + " en posición " + posicion.obtenerCoordenadas());
                        return null;
                    } finally {
                        lock.unlock();
                    }
                })
                .subscribeOn(Schedulers.boundedElastic())
                .onErrorResume(e -> {
                    System.err.println("Error al intentar agregar dinosaurio: " + e.getMessage());
                    return Mono.error(e); // Propagar el error
                }).then();
    }


    public Mono<Void> eliminarDinosaurio(Dinosaurio dino) {
        return Mono.fromCallable(() -> {
                    lock.lock();
                    try {
                        // Find the dinosaur in the island's list
                        Dinosaurio dinoInIsland = dinosaurios.stream()
                                .filter(d -> d.getId().equals(dino.getId()))
                                .findFirst()
                                .orElse(null);

                        if (dinoInIsland == null) {
                            System.err.println("Error: Dinosaurio no se encuentra en la isla.");
                            throw new IllegalArgumentException("El dinosaurio no se encuentra en la isla.");
                        }

                        Posicion posicion = dinoInIsland.getPosicion();
                        if (posicion == null || !esPosicionValida(posicion)) {
                            System.err.println("Error: Dinosaurio no tiene posición válida.");
                            throw new IllegalArgumentException("El dinosaurio no tiene una posición válida.");
                        }
                        if (tablero[posicion.getX()][posicion.getY()] != 1) {
                            System.err.println("Error: El tablero no tiene un dinosaurio en la posición " + posicion.obtenerCoordenadas());
                            throw new IllegalArgumentException("El tablero no tiene un dinosaurio en esa posición.");
                        }
                        // Proceed with removal
                        dinosaurios.remove(dinoInIsland);
                        tablero[posicion.getX()][posicion.getY()] = 0;
                        System.out.println("Dinosaurio eliminado de la isla " + nombre + " en posición " + posicion.obtenerCoordenadas());
                        return null;
                    } finally {
                        lock.unlock();
                    }
                })
                .subscribeOn(Schedulers.boundedElastic())
                .onErrorResume(e -> {
                    System.err.println("Error al intentar eliminar dinosaurio: " + e.getMessage());
                    return Mono.error(e);
                }).then();
    }

    // Verifica si la isla tiene capacidad para más dinosaurios
    public boolean tieneCapacidad() {
        return dinosaurios.size() < capacidadMaxima;
    }

    public boolean esPosicionValida(Posicion posicion) {
        int x = posicion.getX();
        int y = posicion.getY();

        // Asegurarse de que x y y estén dentro de los límites del tablero
        return x >= 0 && x < tablero.length && y >= 0 && y < tablero[0].length;
    }


    public abstract void configurarEntorno();
}
