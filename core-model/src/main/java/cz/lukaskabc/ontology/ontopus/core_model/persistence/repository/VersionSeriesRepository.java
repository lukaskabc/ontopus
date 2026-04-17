package cz.lukaskabc.ontology.ontopus.core_model.persistence.repository;

import cz.lukaskabc.ontology.ontopus.core_model.config.OntopusConfig;
import cz.lukaskabc.ontology.ontopus.core_model.model.id.ResourceURI;
import cz.lukaskabc.ontology.ontopus.core_model.model.id.VersionSeriesURI;
import cz.lukaskabc.ontology.ontopus.core_model.model.ontology.VersionSeries;
import cz.lukaskabc.ontology.ontopus.core_model.persistence.dao.VersionSeriesDao;
import cz.lukaskabc.ontology.ontopus.core_model.persistence.identifier.VersionSeriesUriGenerator;
import cz.lukaskabc.ontology.ontopus.core_model.persistence.repository.base.AbstractRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.Validator;

@Repository
public class VersionSeriesRepository extends AbstractRepository<VersionSeriesURI, VersionSeries, VersionSeriesDao> {
    public VersionSeriesRepository(
            VersionSeriesDao dao,
            Validator validator,
            VersionSeriesUriGenerator identifierGenerator,
            OntopusConfig config) {
        super(dao, validator, identifierGenerator, config);
    }

    /** Checks whether the given ontology identifier exists */
    @Transactional(readOnly = true)
    public boolean isOntologyURI(ResourceURI resourceURI) {
        return dao.isOntologyURI(resourceURI);
    }
}
