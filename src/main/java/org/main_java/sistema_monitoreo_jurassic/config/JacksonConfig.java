package org.main_java.sistema_monitoreo_jurassic.config;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.main_java.sistema_monitoreo_jurassic.model.dinosauriosDTO.carnivoro.CarnivoroDTO;
import org.main_java.sistema_monitoreo_jurassic.model.dinosauriosDTO.herbivoro.HerbivoroDTO;
import org.main_java.sistema_monitoreo_jurassic.model.dinosauriosDTO.omnivoro.OmnivoroDTO;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class JacksonConfig {

    @Bean
    public Jackson2ObjectMapperBuilderCustomizer jacksonCustomizer() {
        return jacksonObjectMapperBuilder -> jacksonObjectMapperBuilder
                .featuresToDisable(
                        DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES,
                        DeserializationFeature.ACCEPT_FLOAT_AS_INT,
                        SerializationFeature.WRITE_DATES_AS_TIMESTAMPS
                );
    }
    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        // Agrega aquí cualquier módulo necesario, o configura para subtipos
        mapper.registerSubtypes(CarnivoroDTO.class, HerbivoroDTO.class, OmnivoroDTO.class);
        return mapper;
    }


}
