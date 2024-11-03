package org.main_java.sistema_monitoreo_jurassic.model;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;

@Data
@Getter
@Setter
public class EventoDTO {
    private String mensaje;
    private Double valor;
    private OffsetDateTime dateCreated = OffsetDateTime.now();
}
