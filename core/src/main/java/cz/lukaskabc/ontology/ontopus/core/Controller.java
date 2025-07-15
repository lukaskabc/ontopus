package cz.lukaskabc.ontology.ontopus.core;

import cz.lukaskabc.ontology.ontopus.api.Plugin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/")
class Controller {
    @Autowired(required = false)
    private List<Plugin> plugins = List.of();


    @GetMapping("plugins")
    public ResponseEntity<?> getPlugins() {
        if (plugins.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(plugins.stream()
                .map(Plugin::getName)
                .toList());
    }

    @GetMapping("core")
    public ResponseEntity<?> get() {
        return ResponseEntity.ok("Core module is running");
    }
}
