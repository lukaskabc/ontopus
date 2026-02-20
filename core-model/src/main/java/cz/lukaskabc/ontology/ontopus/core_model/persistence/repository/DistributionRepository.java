package cz.lukaskabc.ontology.ontopus.core_model.persistence.repository;

import cz.lukaskabc.ontology.ontopus.core_model.model.id.DistributionURI;
import cz.lukaskabc.ontology.ontopus.core_model.model.ontology.OntologyDistribution;
import cz.lukaskabc.ontology.ontopus.core_model.persistence.dao.DistributionDao;
import cz.lukaskabc.ontology.ontopus.core_model.persistence.identifier.IdentifierGenerator;
import cz.lukaskabc.ontology.ontopus.core_model.persistence.repository.base.AbstractRepository;
import org.springframework.validation.Validator;

public class DistributionRepository extends AbstractRepository<DistributionURI, OntologyDistribution, DistributionDao> {
    public DistributionRepository(
            DistributionDao dao,
            Validator validator,
            IdentifierGenerator<DistributionURI, OntologyDistribution> identifierGenerator) {
        super(dao, validator, identifierGenerator);
    }
}
