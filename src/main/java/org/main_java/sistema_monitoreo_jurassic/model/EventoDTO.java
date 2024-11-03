package org.main_java.sistema_monitoreo_jurassic.model;

import lombok.Data;
import java.time.OffsetDateTime;

@Data
public class EventoDTO {
    private Integer id;
    private String sensorTipo;
    private String datos;
    private OffsetDateTime dateCreated;
    private OffsetDateTime lastUpdated;
}
