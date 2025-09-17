package cz.lukaskabc.ontology.ontopus.core.persistence.dao;

import cz.cvut.kbss.jopa.model.EntityManager;
import cz.lukaskabc.ontology.ontopus.core.model.OntologyArtifact;
import cz.lukaskabc.ontology.ontopus.core.model.OntologyArtifact_;
import cz.lukaskabc.ontology.ontopus.core.persistence.DescriptorFactory;
import java.net.URI;
import org.jspecify.annotations.Nullable;
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

    @Nullable public OntologyArtifact findLatestArtifact(URI artifactURI) {
        return resultOrNull(this.em
                .createNativeQuery(
                        """
				SELECT ?artifact WHERE {
				    ?artifact ?hasPreviousVersion* ?prevArtifact .
				    BIND(COALESCE(?artifact, ?prevArtifact) AS ?artifact)
				}
				""",
                        OntologyArtifact.class)
                .setParameter("hasPreviousVersion", OntologyArtifact_.previousVersion)
                .setParameter("prevArtifact", artifactURI)::getSingleResult);
    }

    @Nullable public OntologyArtifact findLatestArtifactFromOntologyIdentifier(URI ontologyIdentifier) {
        return resultOrNull(this.em
                .createNativeQuery(
                        """
				SELECT ?artifact WHERE {
				    ?prevArtifact ?hasOntologyIdentifier ?ontologyIdentifier .
				    ?artifact ?hasPreviousVersion* ?prevArtifact .
				    BIND(COALESCE(?artifact, ?prevArtifact) AS ?artifact)
				}
				""",
                        OntologyArtifact.class)
                .setParameter("ontologyArtifactType", OntologyArtifact_.entityClassIRI)
                .setParameter("hasPreviousVersion", OntologyArtifact_.previousVersion)
                .setParameter("hasOntologyIdentifier", OntologyArtifact_.ontologyIdentifier)
                .setParameter("ontologyIdentifier", ontologyIdentifier)::getSingleResult);
    }
}
