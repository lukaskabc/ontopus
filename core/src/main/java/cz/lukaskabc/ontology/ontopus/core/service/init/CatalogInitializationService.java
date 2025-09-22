package cz.lukaskabc.ontology.ontopus.core.service.init;

import cz.cvut.kbss.jopa.model.MultilingualString;
import cz.lukaskabc.ontology.ontopus.core.config.OntopusConfig;
import cz.lukaskabc.ontology.ontopus.core.model.OntopusCatalog;
import cz.lukaskabc.ontology.ontopus.core.model.id.OntopusCatalogURI;
import cz.lukaskabc.ontology.ontopus.core.persistence.dao.OntopusCatalogDao;
import java.time.Instant;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class CatalogInitializationService implements InitService {
    private static final Logger log = LogManager.getLogger(CatalogInitializationService.class);
    private final OntopusCatalogDao catalogDao;
    private final OntopusConfig.DcatCatalog config;

    @Autowired
    public CatalogInitializationService(OntopusCatalogDao catalogDao, OntopusConfig config) {
        this.catalogDao = catalogDao;
        this.config = config.getDcatCatalog();
    }

    @Override
    @Transactional
    public void init() {
        if (catalogDao.catalogExists(config.getUri())) {
            return;
        }

        final OntopusCatalog catalog = new OntopusCatalog();
        catalog.setIdentifier(new OntopusCatalogURI(config.getUri()));

        // resource
        catalog.setDescription(MultilingualString.create(config.getDescription(), null));
        catalog.setTitle(MultilingualString.create(config.getTitle(), null));
        catalog.setReleaseDate(Instant.now());
        catalog.setModifiedDate(catalog.getReleaseDate());
        catalog.setVersion("latest");

        // dataset
        // TODO catalog distributions
        // some distribution generator? must be connected to plugins somehow

        // catalog
        catalog.setHomepage(config.getUri());

        catalogDao.persist(catalog);
        log.atInfo().log("Initialized ontology catalog {}", catalog.getUri());
    }
}
