package cz.lukaskabc.ontology.ontopus.core_model.service;

import cz.lukaskabc.ontology.ontopus.core_model.model.id.VersionArtifactURI;
import cz.lukaskabc.ontology.ontopus.core_model.model.ontology.VersionArtifact;
import cz.lukaskabc.ontology.ontopus.core_model.persistence.repository.VersionArtifactRepository;
import cz.lukaskabc.ontology.ontopus.core_model.service.base.BaseEntityService;
import org.springframework.stereotype.Service;

@Service
public class VersionArtifactService
        extends BaseEntityService<VersionArtifactURI, VersionArtifact, VersionArtifactRepository> {
    public VersionArtifactService(VersionArtifactRepository repository) {
        super(repository);
    }
}
