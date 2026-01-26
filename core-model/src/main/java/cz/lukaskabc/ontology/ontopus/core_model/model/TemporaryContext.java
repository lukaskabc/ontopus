package cz.lukaskabc.ontology.ontopus.core_model.model;

import cz.cvut.kbss.jopa.model.annotations.OWLClass;
import cz.cvut.kbss.jopa.model.annotations.OWLDataProperty;
import cz.lukaskabc.ontology.ontopus.core_model.generated.Vocabulary;
import cz.lukaskabc.ontology.ontopus.core_model.model.id.TemporaryContextURI;
import jakarta.validation.constraints.NotNull;
import java.time.Instant;

@OWLClass(iri = Vocabulary.s_c_TemporaryContext)
public class TemporaryContext extends PersistenceEntity<TemporaryContextURI> {
    @NotNull @OWLDataProperty(iri = Vocabulary.s_p_dcat_identifier, simpleLiteral = true)
    private TemporaryContextURI identifier;

    @NotNull @OWLDataProperty(iri = Vocabulary.s_p_org_created_at)
    private Instant createdAt;

    public Instant getCreatedAt() {
        return createdAt;
    }

    @Override
    public TemporaryContextURI getIdentifier() {
        return identifier;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public void setIdentifier(TemporaryContextURI identifier) {
        this.identifier = identifier;
    }
}
