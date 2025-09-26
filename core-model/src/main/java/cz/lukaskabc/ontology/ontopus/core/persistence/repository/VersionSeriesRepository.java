package cz.lukaskabc.ontology.ontopus.core.persistence.repository;

import cz.lukaskabc.ontology.ontopus.core.model.VersionSeries;
import cz.lukaskabc.ontology.ontopus.core.model.id.VersionSeriesURI;
import cz.lukaskabc.ontology.ontopus.core.persistence.dao.VersionSeriesDao;
import cz.lukaskabc.ontology.ontopus.core.persistence.identifier.IdentifierGenerator;
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
