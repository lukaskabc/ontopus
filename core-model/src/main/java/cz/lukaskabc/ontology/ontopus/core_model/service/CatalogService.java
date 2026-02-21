package cz.lukaskabc.ontology.ontopus.core_model.service;

import cz.cvut.kbss.jopa.model.MultilingualString;
import cz.lukaskabc.ontology.ontopus.core_model.config.OntopusConfig;
import cz.lukaskabc.ontology.ontopus.core_model.model.ontology.OntopusCatalog;
import cz.lukaskabc.ontology.ontopus.core_model.persistence.repository.CatalogRepository;
import cz.lukaskabc.ontology.ontopus.core_model.util.TimeProvider;
import org.springframework.stereotype.Service;

@Service
public class CatalogService {
    private final CatalogRepository repository;
    private final TimeProvider timeProvider;
    private final OntopusConfig.DcatCatalog config;

    public CatalogService(CatalogRepository repository, TimeProvider timeProvider, OntopusConfig config) {
        this.repository = repository;
        this.timeProvider = timeProvider;
        this.config = config.getDcatCatalog();
    }

    public boolean catalogExists() {
        return repository.catalogExists();
    }

    public OntopusCatalog create() {
        final OntopusCatalog catalog = new OntopusCatalog();
        catalog.setIdentifier(config.getUri());

        // resource
        catalog.setDescription(MultilingualString.create(config.getDescription(), config.getLanguage()));
        catalog.setTitle(MultilingualString.create(config.getTitle(), config.getLanguage()));
        catalog.setReleaseDate(timeProvider.getInstant());
        catalog.setModifiedDate(catalog.getReleaseDate());
        catalog.setVersion("latest");

        // dataset
        // TODO catalog distributions
        // some distribution generator? must be connected to plugins somehow

        // catalog
        catalog.setHomepage(config.getUri().toURI());

        repository.persist(catalog);
        return catalog;
    }
}
