package cz.lukaskabc.ontology.ontopus.core_model.service;

import cz.lukaskabc.ontology.ontopus.core_model.exception.ValidationException;
import cz.lukaskabc.ontology.ontopus.core_model.model.id.ResourceURI;
import cz.lukaskabc.ontology.ontopus.core_model.model.id.VersionArtifactURI;
import cz.lukaskabc.ontology.ontopus.core_model.model.id.VersionSeriesURI;
import cz.lukaskabc.ontology.ontopus.core_model.model.ontology.VersionArtifact;
import cz.lukaskabc.ontology.ontopus.core_model.model.ontology.VersionSeries;
import cz.lukaskabc.ontology.ontopus.core_model.persistence.repository.VersionArtifactRepository;
import cz.lukaskabc.ontology.ontopus.core_model.persistence.repository.VersionSeriesRepository;
import cz.lukaskabc.ontology.ontopus.core_model.service.base.BaseService;
import cz.lukaskabc.ontology.ontopus.core_model.util.TimeProvider;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Service
public class VersionSeriesService extends BaseService<VersionSeriesURI, VersionSeries, VersionSeriesRepository> {
    private static final Logger log = LogManager.getLogger(VersionSeriesService.class);
    private final TimeProvider clockProvider;
    private final VersionArtifactRepository versionArtifactRepository;
    private final CatalogService catalogService;

    public VersionSeriesService(
            VersionSeriesRepository repository,
            TimeProvider clockProvider,
            VersionArtifactRepository versionArtifactRepository,
            CatalogService catalogService) {
        super(repository);
        this.clockProvider = clockProvider;
        this.versionArtifactRepository = versionArtifactRepository;
        this.catalogService = catalogService;
    }

    @Override
    public void deleteById(VersionSeriesURI id) {
        catalogService.removeSeries(id);
        super.deleteById(id);
    }

    /** Checks whether the given ontology identifier exists */
    public boolean isOntologyURI(ResourceURI resourceURI) {
        return repository.isOntologyURI(resourceURI);
    }

    /** Removes the given artifact from series. If there is no more artifacts in the series, the series are deleted. */
    @Transactional
    public void removeMember(VersionArtifact versionArtifact) {
        final VersionArtifactURI artifactURI = versionArtifact.getIdentifier();
        Objects.requireNonNull(artifactURI, "VersionArtifactURI must not be null");
        final VersionSeries series = findRequiredById(versionArtifact.getSeries());
        series.removeMember(artifactURI);

        if (series.getMembers().isEmpty()) {
            delete(series);
            return;
        }

        if (series.getLast().equals(artifactURI)) {
            Objects.requireNonNull(
                    versionArtifact.getPreviousVersion(), "Previous version of Version Artifact cannot be null");
            series.setLast(versionArtifact.getPreviousVersion());
        }

        if (series.getFirst().equals(artifactURI)) {
            versionArtifactRepository
                    .findOldestFromSeriesExcluding(series.getIdentifier(), artifactURI)
                    .ifPresentOrElse(oldest -> series.setFirst(oldest.getIdentifier()), () -> {
                        throw log.throwing(ValidationException.builder()
                                .internalMessage(
                                        "Failed to find oldest version artifact for series " + series.getIdentifier())
                                .detailMessageArguments(new Object[] {series.getIdentifier()})
                                .titleMessageCode("ontopus.core.error.notFound.title")
                                .detailMessageCode("ontopus.core.error.noOldestVersionArtifact")
                                .build());
                    });
        }

        series.setModifiedDate(clockProvider.getInstant());
        update(series);
    }
}
