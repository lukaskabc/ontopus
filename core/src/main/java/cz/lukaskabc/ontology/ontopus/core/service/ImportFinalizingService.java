package cz.lukaskabc.ontology.ontopus.core.service;

import cz.lukaskabc.ontology.ontopus.api.model.ImportProcessContext;
import org.springframework.stereotype.Service;

@Service
public class ImportFinalizingService {
    public void finalize(ImportProcessContext context) {
        // TODO: compile serializable import context
        // backup files for reuse
        // publish event?
    }
}
