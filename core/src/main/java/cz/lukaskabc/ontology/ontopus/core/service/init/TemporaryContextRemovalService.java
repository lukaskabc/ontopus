package cz.lukaskabc.ontology.ontopus.core.service.init;

import cz.lukaskabc.ontology.ontopus.api.service.core.InitializationService;
import cz.lukaskabc.ontology.ontopus.core_model.service.TemporaryContextService;
import org.springframework.stereotype.Component;

@Component
public class TemporaryContextRemovalService implements InitializationService {

    private final TemporaryContextService temporaryContextService;

    public TemporaryContextRemovalService(TemporaryContextService temporaryContextService) {
        this.temporaryContextService = temporaryContextService;
    }

    @Override
    public void initialize() {
        temporaryContextService.deleteAll();
    }
}
