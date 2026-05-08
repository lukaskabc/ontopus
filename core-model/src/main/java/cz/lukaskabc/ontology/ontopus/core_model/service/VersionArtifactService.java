package cz.lukaskabc.ontology.ontopus.core_model.service;

import cz.lukaskabc.ontology.ontopus.core_model.exception.OntopusException;
import cz.lukaskabc.ontology.ontopus.core_model.exception.PersistenceException;
import cz.lukaskabc.ontology.ontopus.core_model.model.id.OntologyVersionURI;
import cz.lukaskabc.ontology.ontopus.core_model.model.id.VersionArtifactURI;
import cz.lukaskabc.ontology.ontopus.core_model.model.id.VersionSeriesURI;
import cz.lukaskabc.ontology.ontopus.core_model.model.ontology.PrefixDeclaration;
import cz.lukaskabc.ontology.ontopus.core_model.model.ontology.VersionArtifact;
import cz.lukaskabc.ontology.ontopus.core_model.persistence.repository.VersionArtifactRepository;
import cz.lukaskabc.ontology.ontopus.core_model.service.base.BaseService;
import cz.lukaskabc.ontology.ontopus.core_model.util.TimeProvider;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class VersionArtifactService
        extends BaseService<VersionArtifactURI, VersionArtifact, VersionArtifactRepository> {
    private static final Logger log = LogManager.getLogger(VersionArtifactService.class);
    private final GraphService graphService;
    private final VersionSeriesService versionSeriesService;
    private final TimeProvider clockProvider;

    public VersionArtifactService(
            VersionArtifactRepository repository,
            GraphService graphService,
            VersionSeriesService versionSeriesService,
            TimeProvider clockProvider) {
        super(repository);
        this.graphService = graphService;
        this.versionSeriesService = versionSeriesService;
        this.clockProvider = clockProvider;
    }

    @Transactional
    @Override
    public void deleteById(VersionArtifactURI id) {
        try {
            final VersionArtifact toDelete = findRequiredById(id);
            versionSeriesService.removeMember(toDelete);

            repository.findByPrevVersion(id).forEach(nextArtifact -> {
                nextArtifact.setPreviousVersion(toDelete.getPreviousVersion());
                nextArtifact.setModifiedDate(clockProvider.getInstant());
                update(nextArtifact);
            });

            super.deleteById(id);
            graphService.deleteGraph(toDelete.getVersionUri());
        } catch (OntopusException ex) {
            throw ex;
        } catch (Exception e) {
            throw log.throwing(PersistenceException.builder()
                    .internalMessage("Failed to delete version artifact with id " + id)
                    .detailMessageArguments(new Object[] {id})
                    .titleMessageCode("ontopus.core.error.failedToDelete.title")
                    .detailMessageCode("ontopus.core.error.failedToDelete.detail")
                    .cause(e)
                    .build());
        }
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
