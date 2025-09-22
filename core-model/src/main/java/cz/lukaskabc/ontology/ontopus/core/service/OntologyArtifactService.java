package cz.lukaskabc.ontology.ontopus.core.service;

import cz.lukaskabc.ontology.ontopus.core.persistence.dao.VersionArtifactDao;
import org.springframework.stereotype.Service;

@Service
public class OntologyArtifactService {
    private final VersionArtifactDao dao;

    public OntologyArtifactService(VersionArtifactDao dao) {
        this.dao = dao;
    }
}
