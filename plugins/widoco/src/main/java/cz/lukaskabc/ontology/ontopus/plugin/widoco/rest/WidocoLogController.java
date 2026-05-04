package cz.lukaskabc.ontology.ontopus.plugin.widoco.rest;

import cz.lukaskabc.ontology.ontopus.core_model.exception.NotFoundException;
import cz.lukaskabc.ontology.ontopus.core_model.exception.OntopusException;
import cz.lukaskabc.ontology.ontopus.plugin.widoco.service.WidocoService;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.nio.file.Path;
import java.util.Objects;
import java.util.UUID;

@RequestMapping(WidocoLogController.PATH)
@RestController
public class WidocoLogController {
    public static final String PATH = "/plugin/widoco/logs";
    private final WidocoService widocoService;

    public WidocoLogController(WidocoService widocoService) {
        this.widocoService = widocoService;
    }

    @GetMapping("/{uuid}")
    public ResponseEntity<FileSystemResource> getLog(@PathVariable("uuid") UUID logUUID) {
        Objects.requireNonNull(logUUID);
        final Path path = widocoService.getLogFile(logUUID);
        if (path != null) {
            return ResponseEntity.status(HttpStatus.OK)
                    .contentType(MediaType.TEXT_PLAIN)
                    .body(new FileSystemResource(path));
        }
        throw NotFoundException.builder()
                .internalMessage("Invalid or non-existent log UUID")
                .detailMessageArguments(OntopusException.EMPTY_ARGUMENTS)
                .build();
    }
}
