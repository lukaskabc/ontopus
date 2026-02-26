package cz.lukaskabc.ontology.ontopus.core_model.persistence.dao;

import cz.cvut.kbss.jopa.model.EntityManager;
import cz.lukaskabc.ontology.ontopus.core_model.model.id.DistributionURI;
import cz.lukaskabc.ontology.ontopus.core_model.model.ontology.OntologyDistribution;
import cz.lukaskabc.ontology.ontopus.core_model.model.ontology.OntologyDistribution_;
import cz.lukaskabc.ontology.ontopus.core_model.persistence.dao.base.AbstractDao;
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
