package cz.lukaskabc.ontology.ontopus.plugin.git.github;

import cz.lukaskabc.ontology.ontopus.plugin.git.model.GithubWebhook;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jspecify.annotations.Nullable;
import org.kohsuke.github.GHEventPayload;
import org.springframework.stereotype.Component;

@Component
public class WebhookHandler {
    private static final Logger log = LogManager.getLogger(WebhookHandler.class);

    private static boolean refDoesNotMatch(GithubWebhook webhook, @Nullable String ref) {
        if (webhook.getRef() == null) {
            return false;
        }
        return ref == null || !webhook.getRef().matcher(ref).matches();
    }

    public void handleGHEvent(GithubWebhook webhook, GHEventPayload.Create createEvent) {
        if (refTypeDoesNotMatch(webhook, createEvent.getRefType())) {
            log.debug(
                    "Received create event with ref type {} does not match required ref type {}, ignoring",
                    createEvent.getRefType(),
                    webhook.getRefType());
            return;
        }
        if (refDoesNotMatch(webhook, createEvent.getRef())) {
            log.debug(
                    "Received create event with ref {} that does not match required pattern, ignoring",
                    createEvent.getRef());
            return;
        }
        log.info("Received create event with ref {}, {}", webhook.getRef(), createEvent.getRef());
        // TODO start import process
    }

    public void handleGHEvent(GithubWebhook webhook, GHEventPayload.Push pushEvent) {
        if (pushEvent.getRef() == null) {
            log.warn("Received push event with null ref, ignoring");
            return;
        }
        if (refDoesNotMatch(webhook, pushEvent.getRef())) {
            log.debug(
                    "Received push event with ref {} that does not match required pattern, ignoring",
                    pushEvent.getRef());
            return;
        }
        log.info("Received push event with ref {}, {}", pushEvent.getRef(), pushEvent.getHead());
        // TODO: start import process
    }

    private boolean refTypeDoesNotMatch(GithubWebhook webhook, @Nullable String refType) {
        return webhook.getRefType() == null || !webhook.getRefType().name().equalsIgnoreCase(refType);
    }
}
