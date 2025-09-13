package cz.lukaskabc.ontology.ontopus.core.service.init;

import cz.cvut.kbss.jopa.model.MultilingualString;
import cz.lukaskabc.ontology.ontopus.core.config.OntopusConfig;
import cz.lukaskabc.ontology.ontopus.core.model.OntologyArtifactCatalog;
import cz.lukaskabc.ontology.ontopus.core.persistance.OntologyArtifactCatalogDao;
import java.time.Instant;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class CatalogInitializationService implements InitService {
    private static final Logger log = LogManager.getLogger(CatalogInitializationService.class);
    private final OntologyArtifactCatalogDao catalogDao;
    private final OntopusConfig.DcatCatalog config;

    @Autowired
    public CatalogInitializationService(OntologyArtifactCatalogDao catalogDao, OntopusConfig config) {
        this.catalogDao = catalogDao;
        this.config = config.getDcatCatalog();
    }

    @Override
    @Transactional
    public void init() {
        if (catalogDao.catalogExists(config.getUri())) {
            return;
        }

        final OntologyArtifactCatalog catalog = new OntologyArtifactCatalog();
        catalog.setUri(config.getUri());
        // resource
        catalog.setDescription(MultilingualString.create(config.getDescription(), null));
        catalog.setTitle(MultilingualString.create(config.getTitle(), null));
        catalog.setReleaseDate(Instant.now());
        catalog.setModifiedDate(catalog.getReleaseDate());
        catalog.setCurrentVersion(config.getUri());
        catalog.setVersion("latest");
        // dataset
        // catalog
        catalog.setHomepage(config.getUri());

        catalogDao.persist(catalog);
        log.atInfo().log("Initialized ontology catalog {}", catalog.getUri());
    }
}
