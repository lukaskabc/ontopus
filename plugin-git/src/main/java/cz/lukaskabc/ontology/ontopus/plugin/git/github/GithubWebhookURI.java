package cz.lukaskabc.ontology.ontopus.plugin.git.github;

import cz.lukaskabc.ontology.ontopus.core_model.model.id.AbstractTypedIdentifier;

import java.net.URI;

public class GithubWebhookURI extends AbstractTypedIdentifier {
    public GithubWebhookURI(String uri) {
        super(uri);
    }

    public GithubWebhookURI(URI uri) {
        super(uri);
    }
}
