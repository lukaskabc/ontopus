package cz.lukaskabc.ontology.ontopus.plugin.widoco.persistence.repository;

import cz.lukaskabc.ontology.ontopus.core_model.model.id.GraphURI;
import cz.lukaskabc.ontology.ontopus.core_model.model.id.OntologyURI;
import cz.lukaskabc.ontology.ontopus.core_model.model.id.OntologyVersionURI;
import cz.lukaskabc.ontology.ontopus.core_model.model.id.ResourceURI;
import cz.lukaskabc.ontology.ontopus.plugin.widoco.persistence.dao.OntologyDao;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.net.URI;
import java.util.Set;

@Repository
public class OntologyRepository {
    private final OntologyDao dao;

    public OntologyRepository(OntologyDao dao) {
        this.dao = dao;
    }

    @Transactional(readOnly = true)
    public URI findPreferredNamespaceByVersionURI(OntologyVersionURI versionURI) {
        return dao.findPreferredNamespaceByVersionURI(versionURI);
    }

    @Transactional(readOnly = true)
    public Set<String> findValue(GraphURI graphURI, OntologyURI subject, ResourceURI predicate) {
        return dao.findValue(graphURI, subject, predicate);
    }
}
