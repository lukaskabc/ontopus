package cz.lukaskabc.ontology.ontopus.core.service.init;

import cz.lukaskabc.ontology.ontopus.api.service.core.InitializationService;
import cz.lukaskabc.ontology.ontopus.core_model.service.TemporaryContextService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

@Component
public class TemporaryContextRemovalService implements InitializationService {
    private static final Logger LOG = LogManager.getLogger(TemporaryContextRemovalService.class);

    private final TemporaryContextService temporaryContextService;

    public TemporaryContextRemovalService(TemporaryContextService temporaryContextService) {
        this.temporaryContextService = temporaryContextService;
    }

    @Override
    public void initialize() {
        LOG.info("Removing all temporary contexts...");
        temporaryContextService.deleteAll();
    }
}
