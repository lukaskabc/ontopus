package cz.lukaskabc.ontology.ontopus.core_model.service;

import cz.lukaskabc.ontology.ontopus.core_model.model.id.OntologyVersionURI;
import cz.lukaskabc.ontology.ontopus.core_model.model.id.VersionArtifactURI;
import cz.lukaskabc.ontology.ontopus.core_model.model.id.VersionSeriesURI;
import cz.lukaskabc.ontology.ontopus.core_model.model.ontology.PrefixDeclaration;
import cz.lukaskabc.ontology.ontopus.core_model.model.ontology.VersionArtifact;
import cz.lukaskabc.ontology.ontopus.core_model.persistence.repository.VersionArtifactRepository;
import cz.lukaskabc.ontology.ontopus.core_model.service.base.BaseService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class VersionArtifactService
        extends BaseService<VersionArtifactURI, VersionArtifact, VersionArtifactRepository> {
    public VersionArtifactService(VersionArtifactRepository repository) {
        super(repository);
    }

    public Page<VersionArtifact> find(VersionSeriesURI seriesURI, Pageable pageable, List<String> filter) {
        return repository.find(seriesURI, pageable, filter);
    }

    public Optional<VersionArtifact> findByVersionUri(OntologyVersionURI versionURI) {
        return repository.findByVersionUri(versionURI);
    }

    public List<PrefixDeclaration> findPrefixDeclarations(OntologyVersionURI ontologyVersionURI) {
        return repository.findPrefixDeclarations(ontologyVersionURI);
    }
}
