package org.main_java.sistema_monitoreo_jurassic.config;

import lombok.SneakyThrows;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.ClassPathResource;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.templateresolver.FileTemplateResolver;

import java.io.File;

/**
 * Carga archivos de Thymeleaf desde el sistema de archivos durante el desarrollo, sin ningún tipo de caché.
 */
@Configuration
@Profile("local")
public class LocalDevConfig {

    @SneakyThrows
    public LocalDevConfig(final TemplateEngine templateEngine) {
        final ClassPathResource applicationYml = new ClassPathResource("application.yml");
        if (applicationYml.isFile()) {
            File sourceRoot = applicationYml.getFile().getParentFile();
            // Busca la raíz del proyecto para configurar la carga de plantillas Thymeleaf
            while (sourceRoot.listFiles((dir, name) -> name.equals("mvnw")).length != 1) {
                sourceRoot = sourceRoot.getParentFile();
            }

            // Configura el resolver de plantillas para cargar archivos Thymeleaf desde el sistema de archivos
            final FileTemplateResolver fileTemplateResolver = new FileTemplateResolver();
            fileTemplateResolver.setPrefix(sourceRoot.getPath() + "/src/main/resources/templates/home");
            fileTemplateResolver.setSuffix(".html");
            fileTemplateResolver.setCacheable(false);
            fileTemplateResolver.setCharacterEncoding("UTF-8");
            fileTemplateResolver.setCheckExistence(true);

            // Aplica el resolver de plantillas al motor de plantillas
            templateEngine.addTemplateResolver(fileTemplateResolver);
        }
    }
}
