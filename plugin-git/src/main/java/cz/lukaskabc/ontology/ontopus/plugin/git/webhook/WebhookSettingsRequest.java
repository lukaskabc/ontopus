package cz.lukaskabc.ontology.ontopus.plugin.git.webhook;

import cz.lukaskabc.ontology.ontopus.plugin.git.webhook.entry.WebhookEntry;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public record WebhookSettingsRequest(@NotNull @Valid List<WebhookEntry> webhooks) {}
