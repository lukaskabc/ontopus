package cz.lukaskabc.ontology.ontopus.core.persistance;

import cz.cvut.kbss.jopa.model.EntityManager;
import cz.lukaskabc.ontology.ontopus.core.model.OntologyArtifact;
import cz.lukaskabc.ontology.ontopus.core.model.OntologyArtifact_;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Validator;

@Component
public class OntologyArtifactDao extends AbstractDao<OntologyArtifact> {
    @Autowired
    public OntologyArtifactDao(EntityManager em, Validator validator, DescriptorFactory descriptorFactory) {
        super(
                OntologyArtifact.class,
                OntologyArtifact_.entityClassIRI.toURI(),
                em,
                validator,
                descriptorFactory.ontologyArtifact());
    }
}
