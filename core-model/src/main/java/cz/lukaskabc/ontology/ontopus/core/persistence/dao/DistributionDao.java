package cz.lukaskabc.ontology.ontopus.core.persistence.dao;

import cz.cvut.kbss.jopa.model.EntityManager;
import cz.lukaskabc.ontology.ontopus.core.model.Distribution;
import cz.lukaskabc.ontology.ontopus.core.model.OntologyDistribution_;
import cz.lukaskabc.ontology.ontopus.core.model.id.DistributionURI;
import cz.lukaskabc.ontology.ontopus.core.persistence.DescriptorFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Validator;

@Component
public class DistributionDao extends AbstractDao<DistributionURI, Distribution> {
    @Autowired
    public DistributionDao(EntityManager em, Validator validator, DescriptorFactory descriptorFactory) {
        super(
                Distribution.class,
                OntologyDistribution_.entityClassIRI.toURI(),
                em,
                validator,
                descriptorFactory.ontologyDistribution(),
                null);
    }
}
