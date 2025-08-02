package cz.lukaskabc.ontology.ontopus.core.service.init;

import cz.lukaskabc.ontology.ontopus.core.service.TemporaryContextRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TemporaryContextRemovalService implements InitService {
    private final TemporaryContextRegistry temporaryContextRegistry;

    @Autowired
    public TemporaryContextRemovalService(TemporaryContextRegistry temporaryContextRegistry) {
        this.temporaryContextRegistry = temporaryContextRegistry;
    }

    @Override
    public void init() {
        temporaryContextRegistry.clearAllTemporaryContexts();
    }
}
