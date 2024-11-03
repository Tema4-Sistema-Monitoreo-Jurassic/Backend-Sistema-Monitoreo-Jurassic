package org.main_java.sistema_monitoreo_jurassic.model;

import lombok.Data;
import java.time.OffsetDateTime;

@Data
public class EventoDTO {
    private String mensaje;
    private Double valor;
    private OffsetDateTime dateCreated = OffsetDateTime.now();
}
