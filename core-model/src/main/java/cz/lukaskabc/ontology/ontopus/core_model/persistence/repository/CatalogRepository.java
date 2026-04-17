package cz.lukaskabc.ontology.ontopus.core_model.persistence.repository;

import cz.lukaskabc.ontology.ontopus.core_model.config.OntopusConfig;
import cz.lukaskabc.ontology.ontopus.core_model.model.id.OntopusCatalogURI;
import cz.lukaskabc.ontology.ontopus.core_model.model.ontology.OntopusCatalog;
import cz.lukaskabc.ontology.ontopus.core_model.persistence.dao.OntopusCatalogDao;
import cz.lukaskabc.ontology.ontopus.core_model.persistence.identifier.CatalogUriUriGenerator;
import cz.lukaskabc.ontology.ontopus.core_model.persistence.repository.base.AbstractRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.Validator;

@Repository
public class CatalogRepository extends AbstractRepository<OntopusCatalogURI, OntopusCatalog, OntopusCatalogDao> {
    private final OntopusCatalogURI catalogUri;

    public CatalogRepository(
            OntopusCatalogDao dao,
            Validator validator,
            CatalogUriUriGenerator catalogUriUriGenerator,
            OntopusConfig config) {
        super(dao, validator, catalogUriUriGenerator, config);
        this.catalogUri = config.getDcatCatalog().getUri();
    }

    @Transactional(readOnly = true)
    public boolean catalogExists() {
        return dao.exists(catalogUri);
    }

    @Transactional(readOnly = true)
    public OntopusCatalog findRequired() {
        return findRequired(catalogUri);
    }
}
