package cz.lukaskabc.ontology.ontopus.core_model.persistence.repository;

import cz.lukaskabc.ontology.ontopus.core_model.model.id.VersionSeriesURI;
import cz.lukaskabc.ontology.ontopus.core_model.model.ontology.VersionSeries;
import cz.lukaskabc.ontology.ontopus.core_model.persistence.dao.VersionSeriesDao;
import cz.lukaskabc.ontology.ontopus.core_model.persistence.identifier.IdentifierGenerator;
import cz.lukaskabc.ontology.ontopus.core_model.persistence.repository.base.AbstractRepository;
import org.springframework.stereotype.Repository;
import org.springframework.validation.Validator;

@Repository
public class VersionSeriesRepository extends AbstractRepository<VersionSeriesURI, VersionSeries, VersionSeriesDao> {
    public VersionSeriesRepository(
            VersionSeriesDao dao,
            Validator validator,
            IdentifierGenerator<VersionSeriesURI, VersionSeries> identifierGenerator) {
        super(dao, validator, identifierGenerator);
    }
}
