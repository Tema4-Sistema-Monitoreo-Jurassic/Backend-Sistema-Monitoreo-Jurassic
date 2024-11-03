package org.main_java.sistema_monitoreo_jurassic.domain;

import lombok.*;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Datos {
    private LocalDateTime timestamp = LocalDateTime.now();
    private Double valor;

    public void procesarDatos() {
        System.out.println("Procesando datos del sensor con valor: " + valor);
    }
}
