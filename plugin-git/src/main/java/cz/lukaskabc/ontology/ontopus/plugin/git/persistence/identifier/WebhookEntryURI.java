package cz.lukaskabc.ontology.ontopus.plugin.git.persistence.identifier;

import cz.lukaskabc.ontology.ontopus.core_model.model.id.AbstractTypedIdentifier;
import cz.lukaskabc.ontology.ontopus.plugin.git.model.WebhookEntry_;

import java.net.URI;
import java.util.UUID;

public class WebhookEntryURI extends AbstractTypedIdentifier {

    private static UUID parseUUID(URI uri) {
        final String s = uri.toString();
        int underscoreIndex = s.lastIndexOf('_');
        if (underscoreIndex == -1 || underscoreIndex == s.length() - 1) {
            throw new IllegalArgumentException("Invalid URI format: UUID not found");
        }
        String uuidPart = s.substring(underscoreIndex + 1);
        final UUID uuid = UUID.fromString(uuidPart);
        if (s.endsWith(uuid.toString())) {
            return uuid;
        } else {
            throw new IllegalArgumentException("Invalid URI format: Failed to parse UUID from the URI");
        }
    }

    private final UUID uuid;

    WebhookEntryURI(URI uri) {
        super(uri);
        this.uuid = parseUUID(uri);
    }

    public WebhookEntryURI(UUID uuid) {
        super(WebhookEntry_.entityClassIRI + "_" + uuid);
        this.uuid = uuid;
    }
}
