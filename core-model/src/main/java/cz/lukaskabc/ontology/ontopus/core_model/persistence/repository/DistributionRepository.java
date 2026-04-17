package cz.lukaskabc.ontology.ontopus.core_model.persistence.repository;

import cz.lukaskabc.ontology.ontopus.core_model.config.OntopusConfig;
import cz.lukaskabc.ontology.ontopus.core_model.model.id.DistributionURI;
import cz.lukaskabc.ontology.ontopus.core_model.model.ontology.OntologyDistribution;
import cz.lukaskabc.ontology.ontopus.core_model.persistence.dao.DistributionDao;
import cz.lukaskabc.ontology.ontopus.core_model.persistence.identifier.DistributionUriGenerator;
import cz.lukaskabc.ontology.ontopus.core_model.persistence.repository.base.AbstractRepository;
import org.springframework.stereotype.Repository;
import org.springframework.validation.Validator;

@Repository
public class DistributionRepository extends AbstractRepository<DistributionURI, OntologyDistribution, DistributionDao> {
    public DistributionRepository(
            DistributionDao dao,
            Validator validator,
            DistributionUriGenerator identifierGenerator,
            OntopusConfig config) {
        super(dao, validator, identifierGenerator, config);
    }
}
