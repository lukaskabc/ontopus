package cz.lukaskabc.ontology.ontopus.core_model.persistence.repository;

import cz.cvut.kbss.jopa.model.MultilingualString;
import cz.lukaskabc.ontology.ontopus.core_model.config.OntopusConfig;
import cz.lukaskabc.ontology.ontopus.core_model.model.id.OntopusCatalogURI;
import cz.lukaskabc.ontology.ontopus.core_model.model.ontology.OntopusCatalog;
import cz.lukaskabc.ontology.ontopus.core_model.persistence.dao.OntopusCatalogDao;
import cz.lukaskabc.ontology.ontopus.core_model.persistence.identifier.IdentifierGenerator;
import cz.lukaskabc.ontology.ontopus.core_model.persistence.repository.base.AbstractRepository;
import cz.lukaskabc.ontology.ontopus.core_model.util.TimeProvider;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.Validator;

@Repository
public class CatalogRepository extends AbstractRepository<OntopusCatalogURI, OntopusCatalog, OntopusCatalogDao> {
    private final OntopusConfig.DcatCatalog config;
    private final OntopusCatalogURI catalogUri;
    private final TimeProvider timeProvider;

    public CatalogRepository(
            OntopusCatalogDao dao,
            Validator validator,
            IdentifierGenerator<OntopusCatalogURI, OntopusCatalog> identifierGenerator,
            OntopusConfig config,
            TimeProvider timeProvider) {
        super(dao, validator, identifierGenerator);
        this.config = config.getDcatCatalog();
        this.timeProvider = timeProvider;
        this.catalogUri = new OntopusCatalogURI(this.config.getUri());
    }

    @Transactional(readOnly = true)
    public boolean catalogExists() {
        return dao.exists(catalogUri);
    }

    @Transactional
    public OntopusCatalog create() {
        final OntopusCatalog catalog = new OntopusCatalog();
        catalog.setIdentifier(new OntopusCatalogURI(config.getUri()));

        // resource
        catalog.setDescription(MultilingualString.create(config.getDescription(), null));
        catalog.setTitle(MultilingualString.create(config.getTitle(), null));
        catalog.setReleaseDate(timeProvider.getInstant());
        catalog.setModifiedDate(catalog.getReleaseDate());
        catalog.setVersion("latest");

        // dataset
        // TODO catalog distributions
        // some distribution generator? must be connected to plugins somehow

        // catalog
        catalog.setHomepage(config.getUri());

        persist(catalog);
        return catalog;
    }

    @Transactional(readOnly = true)
    public OntopusCatalog findRequired() {
        return findRequired(catalogUri);
    }
}
