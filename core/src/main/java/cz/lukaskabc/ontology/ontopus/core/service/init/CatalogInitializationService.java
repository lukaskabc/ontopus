package cz.lukaskabc.ontology.ontopus.core.service.init;

import cz.lukaskabc.ontology.ontopus.api.service.core.InitializationService;
import cz.lukaskabc.ontology.ontopus.core_model.model.ontology.OntopusCatalog;
import cz.lukaskabc.ontology.ontopus.core_model.service.CatalogService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class CatalogInitializationService implements InitializationService {
    private static final Logger log = LogManager.getLogger(CatalogInitializationService.class);
    private final CatalogService service;

    public CatalogInitializationService(CatalogService service) {
        this.service = service;
    }

    @Override
    @Transactional
    public void initialize() {
        OntopusCatalog catalog;
        if (service.catalogExists()) {
            catalog = service.update();
            log.debug("Updated details of existing ontology catalog {}", catalog.getIdentifier());
            return;
        }

        catalog = service.create();
        log.info("Initialized ontology catalog {}", catalog.getIdentifier());
    }
}
