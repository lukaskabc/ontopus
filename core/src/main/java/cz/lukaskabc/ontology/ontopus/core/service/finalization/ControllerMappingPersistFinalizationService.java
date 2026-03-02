package cz.lukaskabc.ontology.ontopus.core.service.finalization;

import cz.lukaskabc.ontology.ontopus.api.model.ImportProcessContext;
import cz.lukaskabc.ontology.ontopus.api.service.ImportFinalizingService;
import cz.lukaskabc.ontology.ontopus.core_model.service.ContextToControllerMappingService;
import org.springframework.stereotype.Service;

/**
 * Persists all {@link cz.lukaskabc.ontology.ontopus.core_model.model.request_mapping.ContextToControllerMapping
 * ContextToControllerMappings} from the {@link ImportProcessContext}.
 *
 * @see ImportProcessContext#getControllerMappings()
 */
@Service
public class ControllerMappingPersistFinalizationService implements ImportFinalizingService {
    private final ContextToControllerMappingService contextToControllerMappingService;

    public ControllerMappingPersistFinalizationService(
            ContextToControllerMappingService contextToControllerMappingService) {
        this.contextToControllerMappingService = contextToControllerMappingService;
    }

    @Override
    public void finalizeImport(ImportProcessContext context) {
        context.getControllerMappings().forEach(contextToControllerMappingService::persist);
    }
}
