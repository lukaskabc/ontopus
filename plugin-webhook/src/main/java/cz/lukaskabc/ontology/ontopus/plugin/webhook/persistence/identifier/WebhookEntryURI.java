package cz.lukaskabc.ontology.ontopus.plugin.webhook.persistence.identifier;

import cz.lukaskabc.ontology.ontopus.core_model.model.id.AbstractTypedIdentifier;

import java.net.URI;

public class WebhookEntryURI extends AbstractTypedIdentifier {
    public WebhookEntryURI(String uri) {
        super(uri);
    }

    public WebhookEntryURI(URI uri) {
        super(uri);
    }
}
