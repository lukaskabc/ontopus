package cz.lukaskabc.ontology.ontopus.core_model.service;

import cz.lukaskabc.ontology.ontopus.core_model.config.OntopusConfig;
import cz.lukaskabc.ontology.ontopus.core_model.model.dcat.Agent;
import cz.lukaskabc.ontology.ontopus.core_model.model.dcat.Agent_;
import cz.lukaskabc.ontology.ontopus.core_model.model.ontology.OntopusCatalog;
import cz.lukaskabc.ontology.ontopus.core_model.persistence.repository.CatalogRepository;
import cz.lukaskabc.ontology.ontopus.core_model.util.TimeProvider;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;

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
        updateDetails(catalog);
        repository.persist(catalog);
        return catalog;
    }

    public OntopusCatalog findRequired() {
        return repository.findRequired();
    }

    @Transactional
    public OntopusCatalog update() {
        OntopusCatalog catalog = repository.findRequired();
        updateDetails(catalog);
        update(catalog);
        return catalog;
    }

    public void update(OntopusCatalog catalog) {
        repository.update(catalog);
    }

    protected void updateDetails(OntopusCatalog catalog) {
        catalog.setIdentifier(config.getUri());

        // resource
        catalog.getDescription().set(config.getLanguage(), config.getDescription());
        catalog.getTitle().set(config.getLanguage(), config.getTitle());
        if (catalog.getReleaseDate() == null) {
            catalog.setReleaseDate(timeProvider.getInstant());
        }
        catalog.setModifiedDate(catalog.getReleaseDate());

        // catalog
        catalog.setHomepage(config.getUri().toURI());

        Agent publisher = catalog.getPublisher();
        ;
        if (publisher == null) {
            publisher = new Agent();
        }

        publisher.getName().set(config.getLanguage(), config.getPublisherName());
        publisher.setTypes(new HashSet<>());
        publisher.getTypes().add(Agent_.entityClassIRI.toURI());
        publisher.getTypes().add(config.getPublisherType());
        catalog.setPublisher(publisher);
    }
}
