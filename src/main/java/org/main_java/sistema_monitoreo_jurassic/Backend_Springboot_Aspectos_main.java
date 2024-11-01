package org.main_java.sistema_monitoreo_jurassic;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication(scanBasePackages = "org.main_java.sistema_monitoreo_jurassic")
public class Backend_Springboot_Aspectos_main {

    public static void main(String[] args) {
        SpringApplication.run(Backend_Springboot_Aspectos_main.class, args);
    }
}
