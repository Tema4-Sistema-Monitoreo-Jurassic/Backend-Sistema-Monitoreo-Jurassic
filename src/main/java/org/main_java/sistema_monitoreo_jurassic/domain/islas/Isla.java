package org.main_java.sistema_monitoreo_jurassic.domain.islas;

import lombok.*;
import org.main_java.sistema_monitoreo_jurassic.domain.dinosaurios.Dinosaurio;
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
    private List<Dinosaurio> dinosaurios = new ArrayList<>();

    public void agregarDinosaurio(Dinosaurio dino) {
        if (dinosaurios.size() < capacidadMaxima) {
            dinosaurios.add(dino);
            System.out.println("Dinosaurio agregado a la isla " + nombre);
        } else {
            System.out.println("Capacidad máxima alcanzada en la isla " + nombre);
        }
    }

    public void eliminarDinosaurio(Dinosaurio dino) {
        dinosaurios.remove(dino);
        System.out.println("Dinosaurio eliminado de la isla " + nombre);
    }

    public boolean tieneCapacidad() {
        return dinosaurios.size() < capacidadMaxima;
    }

    public abstract void configurarEntorno();
}

