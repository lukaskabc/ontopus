package cz.lukaskabc.ontology.ontopus.core.service.init;

import cz.lukaskabc.ontology.ontopus.api.service.core.InitializationService;
import cz.lukaskabc.ontology.ontopus.core_model.model.OntopusCatalog;
import cz.lukaskabc.ontology.ontopus.core_model.persistence.repository.CatalogRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class CatalogInitializationService implements InitializationService {
    private static final Logger log = LogManager.getLogger(CatalogInitializationService.class);
    private final CatalogRepository catalogRepository;

    public CatalogInitializationService(CatalogRepository catalogRepository) {
        this.catalogRepository = catalogRepository;
    }

    @Override
    @Transactional
    public void initialize() {
        if (catalogRepository.catalogExists()) {
            return;
        }

        OntopusCatalog catalog = catalogRepository.create();
        log.atInfo().log("Initialized ontology catalog {}", catalog.getIdentifier());
    }
}
