package cz.lukaskabc.ontology.ontopus.plugin.widoco.rest;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/public/widoco")
@RestController
public class WidocoController {
    private static final Logger log = LogManager.getLogger(WidocoController.class);

    public WidocoController() {
        log.info("WidocoController initialized");
    }

    @GetMapping
    public String ping() {
        return "pong";
    }
}
