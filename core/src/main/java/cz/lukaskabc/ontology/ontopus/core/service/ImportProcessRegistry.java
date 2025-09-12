package cz.lukaskabc.ontology.ontopus.core.service;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

@Service
public class ImportProcessRegistry {
    private final ApplicationEventPublisher publisher;

    public ImportProcessRegistry(ApplicationEventPublisher publisher) {
        this.publisher = publisher;
    }
}
