package cz.lukaskabc.ontology.ontopus.core.persistence.repository;

import cz.cvut.kbss.jopa.model.MultilingualString;
import cz.lukaskabc.ontology.ontopus.core.config.OntopusConfig;
import cz.lukaskabc.ontology.ontopus.core.model.OntopusCatalog;
import cz.lukaskabc.ontology.ontopus.core.model.id.OntopusCatalogURI;
import cz.lukaskabc.ontology.ontopus.core.persistence.dao.OntopusCatalogDao;
import cz.lukaskabc.ontology.ontopus.core.persistence.identifier.IdentifierGenerator;
import java.time.Instant;
import org.springframework.stereotype.Repository;
import org.springframework.validation.Validator;

@Repository
public class CatalogRepository extends AbstractRepository<OntopusCatalogURI, OntopusCatalog, OntopusCatalogDao> {
    private final OntopusConfig.DcatCatalog config;
    private final OntopusCatalogURI catalogUri;

    public CatalogRepository(
            OntopusCatalogDao dao,
            Validator validator,
            IdentifierGenerator<OntopusCatalogURI, OntopusCatalog> identifierGenerator,
            OntopusConfig config) {
        super(dao, validator, identifierGenerator);
        this.config = config.getDcatCatalog();
        this.catalogUri = new OntopusCatalogURI(this.config.getUri());
    }

    public boolean catalogExists() {
        return dao.catalogExists(catalogUri);
    }

    public OntopusCatalog create() {
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

        persist(catalog);
        return catalog;
    }
}
