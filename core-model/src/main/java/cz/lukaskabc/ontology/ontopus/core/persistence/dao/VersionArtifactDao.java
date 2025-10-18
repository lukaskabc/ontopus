package cz.lukaskabc.ontology.ontopus.core.persistence.dao;

import cz.cvut.kbss.jopa.model.EntityManager;
import cz.lukaskabc.ontology.ontopus.core.model.VersionArtifact;
import cz.lukaskabc.ontology.ontopus.core.model.VersionArtifact_;
import cz.lukaskabc.ontology.ontopus.core.model.id.VersionArtifactURI;
import cz.lukaskabc.ontology.ontopus.core.persistence.DescriptorFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class VersionArtifactDao extends AbstractDao<VersionArtifactURI, VersionArtifact> {
    @Autowired
    public VersionArtifactDao(EntityManager em, DescriptorFactory descriptorFactory) {
        super(VersionArtifact.class, VersionArtifact_.entityClassIRI.toURI(), em, descriptorFactory.ontologyArtifact());
    }

    // @Nullable public VersionArtifact findLatestArtifact(URI artifactURI) {
    // return resultOrNull(this.em
    // .createNativeQuery(
    // """
    // SELECT ?artifact WHERE {
    // ?artifact ?hasPreviousVersion* ?prevArtifact .
    // BIND(COALESCE(?artifact, ?prevArtifact) AS ?artifact)
    // }
    // """,
    // VersionArtifact.class)
    // .setParameter("hasPreviousVersion", VersionArtifact_.previousVersion)
    // .setParameter("prevArtifact", artifactURI)::getSingleResult);
    // }
    //
    // @Nullable public VersionArtifact findLatestArtifactFromOntologyIdentifier(URI
    // ontologyIdentifier) {
    // return resultOrNull(this.em
    // .createNativeQuery(
    // """
    // SELECT ?artifact WHERE {
    // ?prevArtifact ?hasOntologyIdentifier ?ontologyIdentifier .
    // ?artifact ?hasPreviousVersion* ?prevArtifact .
    // BIND(COALESCE(?artifact, ?prevArtifact) AS ?artifact)
    // }
    // """,
    // VersionArtifact.class)
    // .setParameter("ontologyArtifactType", VersionArtifact_.entityClassIRI)
    // .setParameter("hasPreviousVersion", VersionArtifact_.previousVersion)
    // .setParameter("hasOntologyIdentifier", VersionArtifact_.ontologyIdentifier)
    // .setParameter("ontologyIdentifier", ontologyIdentifier)::getSingleResult);
    // }
}
