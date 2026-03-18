package cz.lukaskabc.ontology.ontopus.plugin.git.github;

import cz.cvut.kbss.jopa.model.annotations.EnumType;
import cz.cvut.kbss.jopa.model.annotations.Enumerated;
import cz.cvut.kbss.jopa.model.annotations.OWLClass;
import cz.cvut.kbss.jopa.model.annotations.OWLDataProperty;
import cz.lukaskabc.ontology.ontopus.core_model.generated.Vocabulary;
import cz.lukaskabc.ontology.ontopus.core_model.model.AbstractGeneratedPersistenceEntity;
import org.kohsuke.github.GHEvent;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.net.URI;

@OWLClass(iri = Vocabulary.s_c_Webhook)
public class GithubWebhook extends AbstractGeneratedPersistenceEntity<GithubWebhookURI> {

    @OWLDataProperty(iri = Vocabulary.s_p_secret, simpleLiteral = true)
    @NotEmpty private String secret;

    @Enumerated(EnumType.STRING)
    @OWLDataProperty(iri = Vocabulary.s_p_dcat_type, simpleLiteral = true)
    @NotNull private GHEvent event;

    @OWLDataProperty(iri = Vocabulary.s_p_sioc_subject, simpleLiteral = true)
    @NotEmpty private String ref;

    public GHEvent getEvent() {
        return event;
    }

    public String getRef() {
        return ref;
    }

    public String getSecret() {
        return secret;
    }

    public void setEvent(GHEvent event) {
        this.event = event;
    }

    public void setRef(String ref) {
        this.ref = ref;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    @Override
    protected GithubWebhookURI wrapUri(URI uri) {
        return new GithubWebhookURI(uri);
    }
}
