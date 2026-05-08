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
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class VersionArtifactService
        extends BaseService<VersionArtifactURI, VersionArtifact, VersionArtifactRepository> {
    private final GraphService graphService;

    public VersionArtifactService(VersionArtifactRepository repository, GraphService graphService) {
        super(repository);
        this.graphService = graphService;
    }

    @Transactional
    @Override
    public void deleteById(VersionArtifactURI id) {
        super.deleteById(id);
        graphService.deleteGraph(id);
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
