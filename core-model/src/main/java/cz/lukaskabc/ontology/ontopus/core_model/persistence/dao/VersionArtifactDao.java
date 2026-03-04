package cz.lukaskabc.ontology.ontopus.core_model.persistence.dao;

import cz.cvut.kbss.jopa.model.EntityManager;
import cz.lukaskabc.ontology.ontopus.core_model.model.id.VersionArtifactURI;
import cz.lukaskabc.ontology.ontopus.core_model.model.ontology.VersionArtifact;
import cz.lukaskabc.ontology.ontopus.core_model.model.ontology.VersionArtifact_;
import cz.lukaskabc.ontology.ontopus.core_model.persistence.dao.base.AbstractDao;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
public class VersionArtifactDao extends AbstractDao<VersionArtifactURI, VersionArtifact> {
    private static final Logger LOG = LogManager.getLogger(VersionArtifactDao.class);
    private static final int MAX_RECENT = 10;

    @Autowired
    public VersionArtifactDao(EntityManager em, DescriptorFactory descriptorFactory) {
        super(VersionArtifact.class, VersionArtifact_.entityClassIRI, em, descriptorFactory.ontologyArtifact());
    }

    public Stream<VersionArtifact> findAllRecentByIds(Collection<VersionArtifactURI> identifiers) {
        final Set<URI> uris =
                identifiers.stream().map(VersionArtifactURI::toURI).collect(Collectors.toSet());
        try {
            return em.createQuery("SELECT a FROM VersionArtifact a WHERE a.uri IN :ids", VersionArtifact.class)
                    .setParameter("ids", uris)
                    .setMaxResults(MAX_RECENT)
                    .getResultStream();
        } catch (Exception e) {
            throw persistenceException(
                    LOG, "Error finding VersionArtifacts by ids: " + Strings.join(identifiers, ','), e);
        }
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
