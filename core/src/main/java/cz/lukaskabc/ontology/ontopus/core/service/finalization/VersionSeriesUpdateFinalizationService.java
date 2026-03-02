package cz.lukaskabc.ontology.ontopus.core.service.finalization;

import cz.lukaskabc.ontology.ontopus.api.model.ImportProcessContext;
import cz.lukaskabc.ontology.ontopus.api.service.ImportFinalizingService;
import cz.lukaskabc.ontology.ontopus.core_model.model.ontology.VersionArtifact;
import cz.lukaskabc.ontology.ontopus.core_model.model.ontology.VersionSeries;
import cz.lukaskabc.ontology.ontopus.core_model.util.TimeProvider;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

@Service
@Order(FinalizationServiceOrder.VERSION_SERIES_UPDATE)
public class VersionSeriesUpdateFinalizationService implements ImportFinalizingService {
    private final TimeProvider timeProvider;

    public VersionSeriesUpdateFinalizationService(TimeProvider timeProvider) {
        this.timeProvider = timeProvider;
    }

    @Override
    public void finalizeImport(ImportProcessContext context) {
        final VersionArtifact artifact = context.getVersionArtifact();
        final VersionSeries series = context.getVersionSeries();

        final Instant timestamp = timeProvider.getInstant();
        if (series.getLast() != null) {
            artifact.setPreviousVersion(series.getLast());
        }
        artifact.setReleaseDate(timestamp);
        artifact.setModifiedDate(timestamp);
        Objects.requireNonNull(series.getIdentifier(), "Version series identifier must not be null");
        artifact.setSeries(series.getIdentifier());
        Objects.requireNonNull(artifact.getIdentifier(), "Version artifact identifier must not be null");
        series.getMembers().add(artifact.getIdentifier());
        series.setLast(artifact.getIdentifier());
        if (series.getFirst() == null) {
            series.setFirst(artifact.getIdentifier());
        }
        series.setModifiedDate(timestamp);
        if (series.getReleaseDate() == null) {
            series.setReleaseDate(timestamp);
        }

        series.setVersion(timeProvider.getCurrentDateTime().format(DateTimeFormatter.ISO_LOCAL_DATE));
    }
}
