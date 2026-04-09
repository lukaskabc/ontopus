package cz.lukaskabc.ontology.ontopus.core_model.persistence.dao;

import cz.cvut.kbss.jopa.model.EntityManager;
import cz.cvut.kbss.jopa.model.query.criteria.CriteriaBuilder;
import cz.cvut.kbss.jopa.model.query.criteria.CriteriaQuery;
import cz.cvut.kbss.jopa.model.query.criteria.Root;
import cz.lukaskabc.ontology.ontopus.core_model.model.id.VersionArtifactURI;
import cz.lukaskabc.ontology.ontopus.core_model.model.id.VersionSeriesURI;
import cz.lukaskabc.ontology.ontopus.core_model.model.ontology.VersionArtifact;
import cz.lukaskabc.ontology.ontopus.core_model.model.ontology.VersionArtifact_;
import cz.lukaskabc.ontology.ontopus.core_model.persistence.dao.base.AbstractDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class VersionArtifactDao extends AbstractDao<VersionArtifactURI, VersionArtifact> {

    @Autowired
    public VersionArtifactDao(EntityManager em, DescriptorFactory descriptorFactory) {
        super(VersionArtifact.class, VersionArtifact_.entityClassIRI, em, descriptorFactory.ontologyArtifact());
    }

    public long count(VersionSeriesURI seriesURI, List<String> filter) {
        return this.count(filter, (query, cb, root) -> {
            filterBySeries(query, cb, root, seriesURI);
        });
    }

    private void filterBySeries(
            CriteriaQuery<?> query, CriteriaBuilder cb, Root<VersionArtifact> root, VersionSeriesURI seriesURI) {
        query.where(cb.equal(root.getAttr("series"), seriesURI.toURI()));
    }

    public List<VersionArtifact> find(VersionSeriesURI seriesURI, Pageable pageable, List<String> filter) {
        return find(pageable, filter, (query, cb, root) -> {
            filterBySeries(query, cb, root, seriesURI);
        });
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
