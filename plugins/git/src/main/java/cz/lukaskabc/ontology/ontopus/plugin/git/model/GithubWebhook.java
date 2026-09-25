package cz.lukaskabc.ontology.ontopus.plugin.git.model;

import cz.cvut.kbss.jopa.model.annotations.*;
import cz.lukaskabc.ontology.ontopus.core_model.generated.Vocabulary;
import cz.lukaskabc.ontology.ontopus.plugin.git.github.GithubWebhookURI;
import cz.lukaskabc.ontology.ontopus.plugin.git.model.github.GithubEvent;

import jakarta.validation.constraints.NotNull;
import java.net.URI;

@Context(value = Vocabulary.s_c_ontopus_Webhook, propagate = true)
@OWLClass(iri = Vocabulary.s_c_ontopus_GitHub)
public class GithubWebhook extends GitWebhook<GithubWebhookURI> {

    @Enumerated(EnumType.STRING)
    @OWLDataProperty(iri = Vocabulary.s_p_dc_type, simpleLiteral = true)
    @NotNull private GithubEvent event;

    public GithubEvent getEvent() {
        return event;
    }

    public void setEvent(GithubEvent event) {
        this.event = event;
    }

    @Override
    protected GithubWebhookURI wrapUri(URI uri) {
        return new GithubWebhookURI(uri);
    }
}
