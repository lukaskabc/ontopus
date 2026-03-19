package cz.lukaskabc.ontology.ontopus.plugin.git.model;

import cz.cvut.kbss.jopa.model.annotations.*;
import cz.lukaskabc.ontology.ontopus.core_model.generated.Vocabulary;
import cz.lukaskabc.ontology.ontopus.core_model.model.AbstractGeneratedPersistenceEntity;
import cz.lukaskabc.ontology.ontopus.core_model.model.id.VersionSeriesURI;
import cz.lukaskabc.ontology.ontopus.plugin.git.github.GithubWebhookURI;
import org.kohsuke.github.GHEvent;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.net.URI;
import java.util.regex.Pattern;

@OWLClass(iri = Vocabulary.s_c_Webhook)
public class GithubWebhook extends AbstractGeneratedPersistenceEntity<GithubWebhookURI> {

    @NotNull @OWLObjectProperty(iri = Vocabulary.s_p_sioc_related_to)
    private URI versionSeries;

    @OWLDataProperty(iri = Vocabulary.s_p_secret, simpleLiteral = true)
    @NotEmpty private String secret;

    @Enumerated(EnumType.STRING)
    @OWLDataProperty(iri = Vocabulary.s_p_dcat_type, simpleLiteral = true)
    @NotNull private GHEvent event;

    @OWLDataProperty(iri = Vocabulary.s_p_regexPattern, simpleLiteral = true)
    private Pattern ref;

    @Enumerated(EnumType.STRING)
    @OWLDataProperty(iri = Vocabulary.s_p_sioc_about, simpleLiteral = true)
    private RefType refType;

    public GHEvent getEvent() {
        return event;
    }

    public Pattern getRef() {
        return ref;
    }

    public RefType getRefType() {
        return refType;
    }

    public String getSecret() {
        return secret;
    }

    public VersionSeriesURI getVersionSeries() {
        return new VersionSeriesURI(versionSeries);
    }

    public void setEvent(GHEvent event) {
        this.event = event;
    }

    public void setRef(Pattern ref) {
        this.ref = ref;
    }

    public void setRefType(RefType refType) {
        this.refType = refType;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public void setVersionSeries(VersionSeriesURI versionSeries) {
        this.versionSeries = versionSeries.toURI();
    }

    @Override
    protected GithubWebhookURI wrapUri(URI uri) {
        return new GithubWebhookURI(uri);
    }
}
