package org.main_java.sistema_monitoreo_jurassic.model;

import lombok.*;

import java.time.OffsetDateTime;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EventoDTO {
    private String mensaje;
    private Double valor;
}
