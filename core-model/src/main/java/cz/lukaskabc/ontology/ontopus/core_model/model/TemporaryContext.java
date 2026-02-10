package cz.lukaskabc.ontology.ontopus.core_model.model;

import cz.cvut.kbss.jopa.model.annotations.OWLClass;
import cz.cvut.kbss.jopa.model.annotations.OWLDataProperty;
import cz.lukaskabc.ontology.ontopus.core_model.generated.Vocabulary;
import cz.lukaskabc.ontology.ontopus.core_model.model.id.TemporaryContextURI;

import jakarta.validation.constraints.NotNull;
import java.net.URI;
import java.time.Instant;

@OWLClass(iri = Vocabulary.s_c_TemporaryContext)
public class TemporaryContext extends PersistenceEntity<TemporaryContextURI> {

    @NotNull @OWLDataProperty(iri = Vocabulary.s_p_sioc_created_at)
    private Instant createdAt;

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    protected TemporaryContextURI wrapUri(URI uri) {
        return new TemporaryContextURI(uri);
    }
}
