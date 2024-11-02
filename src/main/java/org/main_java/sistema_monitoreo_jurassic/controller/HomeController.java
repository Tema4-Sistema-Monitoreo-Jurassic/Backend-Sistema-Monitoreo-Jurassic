package org.main_java.sistema_monitoreo_jurassic.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/home")
public class HomeController {

    @GetMapping
    public Mono<String> index() {
        return Mono.just("Welcome to the Jurassic Monitoring System!");
    }
}
