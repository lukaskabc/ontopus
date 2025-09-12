package cz.lukaskabc.ontology.ontopus.core.persistance;

import cz.cvut.kbss.jopa.model.EntityManager;
import cz.lukaskabc.ontology.ontopus.core.model.OntologyDistribution;
import cz.lukaskabc.ontology.ontopus.core.model.OntologyDistribution_;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Validator;

@Component
public class OntologyDistributionDao extends AbstractDao<OntologyDistribution> {
    @Autowired
    public OntologyDistributionDao(EntityManager em, Validator validator, DescriptorFactory descriptorFactory) {
        super(
                OntologyDistribution.class,
                OntologyDistribution_.entityClassIRI.toURI(),
                em,
                validator,
                descriptorFactory.ontologyDistribution());
    }
}
