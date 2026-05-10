package cz.lukaskabc.ontology.ontopus.core_model.service;

import cz.lukaskabc.ontology.ontopus.core_model.config.OntopusConfig;
import cz.lukaskabc.ontology.ontopus.core_model.model.dcat.Agent;
import cz.lukaskabc.ontology.ontopus.core_model.model.dcat.Agent_;
import cz.lukaskabc.ontology.ontopus.core_model.model.id.VersionSeriesURI;
import cz.lukaskabc.ontology.ontopus.core_model.model.ontology.OntopusCatalog;
import cz.lukaskabc.ontology.ontopus.core_model.persistence.repository.CatalogRepository;
import cz.lukaskabc.ontology.ontopus.core_model.util.DcatIdentifierProvider;
import cz.lukaskabc.ontology.ontopus.core_model.util.TimeProvider;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;

@Service
public class CatalogService {
    private final CatalogRepository repository;
    private final TimeProvider timeProvider;
    private final OntopusConfig.DcatCatalog config;
    private final DcatIdentifierProvider identifierProvider;

    public CatalogService(
            CatalogRepository repository,
            TimeProvider timeProvider,
            OntopusConfig config,
            DcatIdentifierProvider identifierProvider) {
        this.repository = repository;
        this.timeProvider = timeProvider;
        this.config = config.getDcatCatalog();
        this.identifierProvider = identifierProvider;
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
    public void removeSeries(VersionSeriesURI versionSeriesURI) {
        final OntopusCatalog catalog = findRequired();
        catalog.removeVersionSeries(versionSeriesURI);
        update(catalog);
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
        catalog.setIdentifier(identifierProvider.getCatalogUri());

        // resource
        catalog.getDescription().set(config.getLanguage(), config.getDescription());
        catalog.getTitle().set(config.getLanguage(), config.getTitle());
        if (catalog.getReleaseDate() == null) {
            catalog.setReleaseDate(timeProvider.getInstant());
        }
        catalog.setModifiedDate(catalog.getReleaseDate());

        // catalog
        catalog.setHomepage(catalog.getIdentifier().toURI());

        Agent publisher = catalog.getPublisher();
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
