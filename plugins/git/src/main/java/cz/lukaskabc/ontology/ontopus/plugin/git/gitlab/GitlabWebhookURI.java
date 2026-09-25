package cz.lukaskabc.ontology.ontopus.plugin.git.gitlab;

import cz.lukaskabc.ontology.ontopus.core_model.model.id.AbstractTypedIdentifier;

import java.net.URI;

public class GitlabWebhookURI extends AbstractTypedIdentifier {
    public GitlabWebhookURI(String uri) {
        super(uri);
    }

    public GitlabWebhookURI(URI uri) {
        super(uri);
    }
}
