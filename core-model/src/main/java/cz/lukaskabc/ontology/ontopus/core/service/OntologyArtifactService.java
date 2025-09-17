package cz.lukaskabc.ontology.ontopus.core.service;

import cz.lukaskabc.ontology.ontopus.core.persistence.dao.OntologyArtifactDao;
import org.springframework.stereotype.Service;

@Service
public class OntologyArtifactService {
    private final OntologyArtifactDao dao;

    public OntologyArtifactService(OntologyArtifactDao dao) {
        this.dao = dao;
    }
}
