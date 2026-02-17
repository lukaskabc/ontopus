package cz.lukaskabc.ontology.ontopus.core_model.persistence.dao;

import cz.cvut.kbss.jopa.model.EntityManager;
import cz.lukaskabc.ontology.ontopus.core_model.model.OntologyDistribution;
import cz.lukaskabc.ontology.ontopus.core_model.model.OntologyDistribution_;
import cz.lukaskabc.ontology.ontopus.core_model.model.id.DistributionURI;
import cz.lukaskabc.ontology.ontopus.core_model.persistence.DescriptorFactory;
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
