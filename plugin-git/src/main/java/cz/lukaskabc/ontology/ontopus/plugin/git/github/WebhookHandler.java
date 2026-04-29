package cz.lukaskabc.ontology.ontopus.plugin.git.github;

import cz.lukaskabc.ontology.ontopus.api.service.core.ImportInitiationService;
import cz.lukaskabc.ontology.ontopus.core_model.exception.InternalException;
import cz.lukaskabc.ontology.ontopus.core_model.exception.OntopusException;
import cz.lukaskabc.ontology.ontopus.core_model.generated.Vocabulary;
import cz.lukaskabc.ontology.ontopus.core_model.model.id.VersionSeriesURI;
import cz.lukaskabc.ontology.ontopus.core_model.model.ontology.VersionSeries;
import cz.lukaskabc.ontology.ontopus.core_model.model.util.ImportProcessContextRequest;
import cz.lukaskabc.ontology.ontopus.core_model.service.VersionSeriesService;
import cz.lukaskabc.ontology.ontopus.plugin.git.model.GithubWebhook;
import cz.lukaskabc.ontology.ontopus.plugin.git.model.github.GithubCreateEvent;
import cz.lukaskabc.ontology.ontopus.plugin.git.model.github.GithubPushEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jspecify.annotations.Nullable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.concurrent.Future;

@Component
public class WebhookHandler {
    private static final Logger log = LogManager.getLogger(WebhookHandler.class);

    private static boolean refDoesNotMatch(GithubWebhook webhook, @Nullable String ref) {
        if (webhook.getRef() == null) {
            return false;
        }
        return ref == null || !webhook.getRef().matcher(ref).matches();
    }

    private final ImportInitiationService importService;
    private final VersionSeriesService versionSeriesService;

    public WebhookHandler(ImportInitiationService importService, VersionSeriesService versionSeriesService) {
        this.importService = importService;
        this.versionSeriesService = versionSeriesService;
    }

    public ResponseEntity<Void> handleGHEvent(GithubWebhook webhook, GithubCreateEvent createEvent) {
        if (refTypeDoesNotMatch(webhook, createEvent.getRefType())) {
            log.debug(
                    "Received create event with ref type '{}' does not match required ref type '{}', ignoring",
                    createEvent.getRefType(),
                    webhook.getRefType());
            return ResponseEntity.noContent().build();
        }
        if (refDoesNotMatch(webhook, createEvent.getRef())) {
            log.debug(
                    "Received create event with ref '{}' that does not match required pattern, ignoring",
                    createEvent.getRef());
            return ResponseEntity.noContent().build();
        }
        log.info("Received create event with ref '{}'", createEvent.getRef());
        initiateImport(webhook.getVersionSeries());
        return ResponseEntity.accepted().build();
    }

    public ResponseEntity<Void> handleGHEvent(GithubWebhook webhook, GithubPushEvent pushEvent) {
        if (pushEvent.getRef() == null) {
            log.warn("Received push event with null ref, ignoring");
            return ResponseEntity.noContent().build();
        }
        if (refDoesNotMatch(webhook, pushEvent.getRef())) {
            log.debug(
                    "Received push event with ref '{}' that does not match required pattern, ignoring",
                    pushEvent.getRef());
            return ResponseEntity.noContent().build();
        }
        log.info("Received push event with ref '{}'", pushEvent.getRef());
        initiateImport(webhook.getVersionSeries());
        return ResponseEntity.accepted().build();
    }

    private void initiateImport(VersionSeriesURI seriesURI) {
        Objects.requireNonNull(seriesURI, "Version series URI must not be null");
        final VersionSeries series = versionSeriesService.findRequiredById(seriesURI);
        final ImportProcessContextRequest contextRequest = new ImportProcessContextRequest();
        contextRequest.setVersionSeriesURI(seriesURI);
        contextRequest.setSerializableImportProcessContext(series.getSerializableImportProcessContext());
        Future<@Nullable Void> scheduleFuture = importService.submitCombinedData(contextRequest);
        if (scheduleFuture.isCancelled() || scheduleFuture.state().equals(Future.State.FAILED)) {
            throw log.throwing(InternalException.builder()
                    .errorType(Vocabulary.u_i_internal_error)
                    .internalMessage("Failed to submit combined data, future state: "
                            + scheduleFuture.state().name())
                    .detailMessageArguments(OntopusException.EMPTY_ARGUMENTS)
                    .build());
        }
    }

    private boolean refTypeDoesNotMatch(GithubWebhook webhook, @Nullable String refType) {
        return webhook.getRefType() == null || !webhook.getRefType().name().equalsIgnoreCase(refType);
    }
}
