package cz.lukaskabc.ontology.ontopus.core.import_process.ordered;

import cz.lukaskabc.ontology.ontopus.api.model.ImportProcessContext;
import cz.lukaskabc.ontology.ontopus.api.model.JsonForm;
import cz.lukaskabc.ontology.ontopus.api.model.ReadOnlyImportProcessContext;
import cz.lukaskabc.ontology.ontopus.api.service.import_process.OrderedImportPipelineService;
import cz.lukaskabc.ontology.ontopus.core.import_process.ImportProcessServiceOrder;
import cz.lukaskabc.ontology.ontopus.core_model.exception.JsonFormSubmitException;
import cz.lukaskabc.ontology.ontopus.core_model.model.id.VersionArtifactURI;
import cz.lukaskabc.ontology.ontopus.core_model.model.id.VersionSeriesURI;
import cz.lukaskabc.ontology.ontopus.core_model.model.ontology.VersionArtifact;
import cz.lukaskabc.ontology.ontopus.core_model.model.ontology.VersionSeries;
import cz.lukaskabc.ontology.ontopus.core_model.model.util.FormResult;
import cz.lukaskabc.ontology.ontopus.core_model.persistence.identifier.VersionArtifactUriGenerator;
import cz.lukaskabc.ontology.ontopus.core_model.persistence.identifier.VersionSeriesUriGenerator;
import cz.lukaskabc.ontology.ontopus.core_model.util.TimeProvider;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jspecify.annotations.Nullable;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;
import tools.jackson.databind.JsonNode;

import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * Updates the {@link ImportProcessContext#versionSeries VersionSeries} and {@link ImportProcessContext#versionArtifact
 * VersionArtifact} setting creation and modification timestamps and connecting the previous version chain.
 */
@Service
@Order(ImportProcessServiceOrder.VERSION_SERIES_UPDATE)
public class VersionSeriesAndArtifactUpdatingService implements OrderedImportPipelineService<Void> {
    private static final Logger log = LogManager.getLogger(VersionSeriesAndArtifactUpdatingService.class);
    private final TimeProvider timeProvider;
    private final VersionSeriesUriGenerator versionSeriesUriGenerator;
    private final VersionArtifactUriGenerator versionArtifactUriGenerator;

    public VersionSeriesAndArtifactUpdatingService(
            TimeProvider timeProvider,
            VersionSeriesUriGenerator versionSeriesUriGenerator,
            VersionArtifactUriGenerator versionArtifactUriGenerator) {
        this.timeProvider = timeProvider;
        this.versionSeriesUriGenerator = versionSeriesUriGenerator;
        this.versionArtifactUriGenerator = versionArtifactUriGenerator;
    }

    @Override
    public @Nullable JsonForm getJsonForm(ReadOnlyImportProcessContext context, @Nullable JsonNode previousFormData) {
        return null;
    }

    @Override
    public String getServiceName() {
        return this.getClass().getName();
    }

    @Override
    public Void handleSubmit(FormResult formResult, ImportProcessContext context) throws JsonFormSubmitException {
        log.debug("Updating version series and artifact");
        final VersionArtifact artifact = context.getVersionArtifact();
        final VersionSeries series = context.getVersionSeries();

        if (series.getIdentifier() == null) {
            VersionSeriesURI seriesURI = versionSeriesUriGenerator.generate(series);
            series.setIdentifier(seriesURI);
        }
        if (artifact.getIdentifier() == null) {
            VersionArtifactURI artifactURI = versionArtifactUriGenerator.generate(artifact);
            artifact.setIdentifier(artifactURI);
        }

        final Instant timestamp = timeProvider.getInstant();

        updateArtifact(artifact, series, timestamp);
        updateSeries(artifact, series, timestamp);

        return null;
    }

    private <T> void setIfMissing(Consumer<T> setter, Supplier<T> getter, T value) {
        if (getter.get() == null) {
            setter.accept(value);
        }
    }

    private void updateArtifact(VersionArtifact artifact, VersionSeries series, Instant timestamp) {
        final VersionArtifactURI previous = series.getLast();
        final boolean isLatest = Objects.equals(previous, artifact.getIdentifier());
        final boolean isFirst = Objects.equals(series.getFirst(), artifact.getIdentifier());

        if (!isLatest && !isFirst) {
            // set if no previous version is known
            setIfMissing(artifact::setPreviousVersion, artifact::getPreviousVersion, previous);
        }

        setIfMissing(artifact::setReleaseDate, artifact::getReleaseDate, timestamp);
        artifact.setModifiedDate(timestamp);
        Objects.requireNonNull(series.getIdentifier(), "Version series identifier must not be null");
        artifact.setSeries(series.getIdentifier());

        Objects.requireNonNull(artifact.getIdentifier(), "Version artifact identifier must not be null");
        series.addMember(artifact.getIdentifier());
    }

    private void updateSeries(VersionArtifact artifact, VersionSeries series, Instant timestamp) {
        Objects.requireNonNull(artifact.getIdentifier());
        final boolean isPreviousVersionLatest = Objects.equals(artifact.getPreviousVersion(), series.getLast());

        if (isPreviousVersionLatest) {
            series.setLast(artifact.getIdentifier());
        }

        setIfMissing(series::setFirst, series::getFirst, artifact.getIdentifier());
        series.setModifiedDate(timestamp);
        setIfMissing(series::setReleaseDate, series::getReleaseDate, timestamp);

        series.setVersion(timeProvider.getCurrentDateTime().format(DateTimeFormatter.ISO_LOCAL_DATE));
    }
}
