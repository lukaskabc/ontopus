package cz.lukaskabc.ontology.ontopus.core.persistence.dao;

import cz.cvut.kbss.jopa.model.EntityManager;
import cz.lukaskabc.ontology.ontopus.core.model.OntologyDistribution;
import cz.lukaskabc.ontology.ontopus.core.model.OntologyDistribution_;
import cz.lukaskabc.ontology.ontopus.core.model.id.DistributionURI;
import cz.lukaskabc.ontology.ontopus.core.persistence.DescriptorFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DistributionDao extends AbstractDao<DistributionURI, OntologyDistribution> {
    @Autowired
    public DistributionDao(EntityManager em, DescriptorFactory descriptorFactory) {
        super(
                OntologyDistribution.class,
                OntologyDistribution_.entityClassIRI.toURI(),
                em,
                descriptorFactory.ontologyDistribution());
    }
}
