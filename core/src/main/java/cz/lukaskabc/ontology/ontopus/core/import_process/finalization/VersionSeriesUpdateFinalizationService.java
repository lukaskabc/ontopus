package cz.lukaskabc.ontology.ontopus.core.import_process.finalization;

import cz.lukaskabc.ontology.ontopus.api.model.ImportProcessContext;
import cz.lukaskabc.ontology.ontopus.api.service.ImportFinalizingService;
import cz.lukaskabc.ontology.ontopus.core_model.model.id.VersionArtifactURI;
import cz.lukaskabc.ontology.ontopus.core_model.model.id.VersionSeriesURI;
import cz.lukaskabc.ontology.ontopus.core_model.model.ontology.VersionArtifact;
import cz.lukaskabc.ontology.ontopus.core_model.model.ontology.VersionSeries;
import cz.lukaskabc.ontology.ontopus.core_model.persistence.identifier.VersionArtifactUriGenerator;
import cz.lukaskabc.ontology.ontopus.core_model.persistence.identifier.VersionSeriesUriGenerator;
import cz.lukaskabc.ontology.ontopus.core_model.util.TimeProvider;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Supplier;

@Service
@Order(FinalizationServiceOrder.VERSION_SERIES_UPDATE)
public class VersionSeriesUpdateFinalizationService implements ImportFinalizingService {
    private final TimeProvider timeProvider;
    private final VersionSeriesUriGenerator versionSeriesUriGenerator;
    private final VersionArtifactUriGenerator versionArtifactUriGenerator;

    public VersionSeriesUpdateFinalizationService(
            TimeProvider timeProvider,
            VersionSeriesUriGenerator versionSeriesUriGenerator,
            VersionArtifactUriGenerator versionArtifactUriGenerator) {
        this.timeProvider = timeProvider;
        this.versionSeriesUriGenerator = versionSeriesUriGenerator;
        this.versionArtifactUriGenerator = versionArtifactUriGenerator;
    }

    @Override
    public void finalizeImport(ImportProcessContext context) {
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
        final VersionArtifactURI previous = series.getLast();

        // if the latest version is not this version
        if (!Objects.equals(previous, artifact.getIdentifier())) {
            // set if no previous version is known
            setIfMissing(artifact::setPreviousVersion, artifact::getPreviousVersion, previous);
        }

        setIfMissing(artifact::setReleaseDate, artifact::getReleaseDate, timestamp);
        artifact.setModifiedDate(timestamp);
        Objects.requireNonNull(series.getIdentifier(), "Version series identifier must not be null");
        artifact.setSeries(series.getIdentifier());

        Objects.requireNonNull(artifact.getIdentifier(), "Version artifact identifier must not be null");
        series.addMember(artifact.getIdentifier());

        // if the latest version is the previous version for this new version
        // this new version becomes the latest
        if (Objects.equals(artifact.getPreviousVersion(), series.getLast())) {
            series.setLast(artifact.getIdentifier());
        }

        setIfMissing(series::setFirst, series::getFirst, artifact.getIdentifier());
        series.setModifiedDate(timestamp);
        setIfMissing(series::setReleaseDate, series::getReleaseDate, timestamp);

        series.setVersion(timeProvider.getCurrentDateTime().format(DateTimeFormatter.ISO_LOCAL_DATE));
    }

    private <T> void setIfMissing(Consumer<T> setter, Supplier<T> getter, T value) {
        if (getter.get() == null) {
            setter.accept(value);
        }
    }
}
