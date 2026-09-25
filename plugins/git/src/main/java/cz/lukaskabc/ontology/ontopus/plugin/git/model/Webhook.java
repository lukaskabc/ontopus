package cz.lukaskabc.ontology.ontopus.plugin.git.model;

import cz.cvut.kbss.jopa.model.annotations.Context;
import cz.cvut.kbss.jopa.model.annotations.MappedSuperclass;
import cz.cvut.kbss.jopa.model.annotations.OWLDataProperty;
import cz.cvut.kbss.jopa.model.annotations.OWLObjectProperty;
import cz.lukaskabc.ontology.ontopus.core_model.generated.Vocabulary;
import cz.lukaskabc.ontology.ontopus.core_model.model.AbstractGeneratedPersistenceEntity;
import cz.lukaskabc.ontology.ontopus.core_model.model.id.TypedIdentifier;
import cz.lukaskabc.ontology.ontopus.core_model.model.id.VersionSeriesURI;
import cz.lukaskabc.ontology.ontopus.core_model.model.util.DocumentedOWLClass;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.net.URI;

@MappedSuperclass
@Context(value = Vocabulary.s_c_ontopus_Webhook, propagate = true)
@DocumentedOWLClass(iri = Vocabulary.s_c_ontopus_Webhook)
public abstract class Webhook<I extends TypedIdentifier> extends AbstractGeneratedPersistenceEntity<I> {
    @NotNull @OWLObjectProperty(iri = Vocabulary.s_p_sioc_related_to)
    private URI versionSeries;

    @OWLDataProperty(iri = Vocabulary.s_p_ontopus_secret, simpleLiteral = true)
    @NotEmpty private String secret;

    public String getSecret() {
        return secret;
    }

    public VersionSeriesURI getVersionSeries() {
        return new VersionSeriesURI(versionSeries);
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public void setVersionSeries(VersionSeriesURI versionSeries) {
        this.versionSeries = versionSeries.toURI();
    }
}
