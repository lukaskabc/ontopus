package cz.lukaskabc.ontology.ontopus.plugin.git.model;

import cz.cvut.kbss.jopa.model.annotations.*;
import cz.lukaskabc.ontology.ontopus.core_model.generated.Vocabulary;
import cz.lukaskabc.ontology.ontopus.plugin.git.gitlab.GitlabWebhookURI;
import cz.lukaskabc.ontology.ontopus.plugin.git.model.gitlab.GitlabEvent;

import jakarta.validation.constraints.NotNull;
import java.net.URI;

/**
 * GitLab webhook configuration
 *
 * <p>{@link #secret} stores the GitLab generated signing token
 */
@Context(value = Vocabulary.s_c_ontopus_Webhook, propagate = true)
@OWLClass(iri = Vocabulary.s_c_ontopus_GitLab)
public class GitlabWebhook extends GitWebhook<GitlabWebhookURI> {
    @Enumerated(EnumType.STRING)
    @OWLDataProperty(iri = Vocabulary.s_p_dc_type, simpleLiteral = true)
    @NotNull private GitlabEvent event;

    public GitlabEvent getEvent() {
        return event;
    }

    public void setEvent(GitlabEvent event) {
        this.event = event;
    }

    @Override
    protected GitlabWebhookURI wrapUri(URI uri) {
        return new GitlabWebhookURI(uri);
    }
}
