package org.main_java.sistema_monitoreo_jurassic.repos;

import org.main_java.sistema_monitoreo_jurassic.domain.Evento;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface EventoRepository extends ReactiveMongoRepository<Evento, String> {
}