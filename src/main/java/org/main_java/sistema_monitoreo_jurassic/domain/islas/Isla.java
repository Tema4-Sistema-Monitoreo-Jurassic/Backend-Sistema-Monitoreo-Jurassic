package org.main_java.sistema_monitoreo_jurassic.domain.islas;

import lombok.*;
import org.main_java.sistema_monitoreo_jurassic.domain.dinosaurios.Dinosaurio;
import org.main_java.sistema_monitoreo_jurassic.domain.dinosaurios.Posicion;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;

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
    private List<Dinosaurio> dinosaurios = new ArrayList<>();

    public Isla(String id, String nombre, int capacidadMaxima, int tamanioTablero) {
        this.id = id;
        this.nombre = nombre;
        this.capacidadMaxima = capacidadMaxima;
        this.tamanioTablero = tamanioTablero;
        this.tablero = new int[tamanioTablero][tamanioTablero];
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

    // Agrega un dinosaurio a una posición específica en el tablero
    public void agregarDinosaurio(Dinosaurio dino, Posicion posicion) {
        if (tieneCapacidad() && esPosicionValida(posicion) && tablero[posicion.getX()][posicion.getY()] == 0) {
            dinosaurios.add(dino);
            tablero[posicion.getX()][posicion.getY()] = 1; // Marca la posición como ocupada
            dino.setPosicion(posicion); // Asigna la posición al dinosaurio
            System.out.println("Dinosaurio agregado a la isla " + nombre + " en posición " + posicion.obtenerCoordenadas());
        } else {
            System.out.println("No se pudo agregar el dinosaurio a la isla " + nombre + ". Posición ocupada o capacidad máxima alcanzada.");
        }
    }

    // Elimina un dinosaurio del tablero
    public void eliminarDinosaurio(Dinosaurio dino) {
        Posicion posicion = dino.getPosicion();
        if (posicion != null && esPosicionValida(posicion) && tablero[posicion.getX()][posicion.getY()] == 1) {
            dinosaurios.remove(dino);
            tablero[posicion.getX()][posicion.getY()] = 0; // Marca la posición como vacía
            System.out.println("Dinosaurio eliminado de la isla " + nombre + " en posición " + posicion.obtenerCoordenadas());
        } else {
            System.out.println("No se pudo eliminar el dinosaurio de la isla " + nombre + ". Posición no válida o no ocupada.");
        }
    }

    // Verifica si la isla tiene capacidad para más dinosaurios
    public boolean tieneCapacidad() {
        return dinosaurios.size() < capacidadMaxima;
    }

    // Verifica si una posición está dentro de los límites del tablero
    public boolean esPosicionValida(Posicion posicion) {
        return posicion.getX() >= 0 && posicion.getX() < tamanioTablero &&
                posicion.getY() >= 0 && posicion.getY() < tamanioTablero;
    }

    public abstract void configurarEntorno();
}
