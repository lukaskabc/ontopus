package cz.lukaskabc.ontology.ontopus.core.model;

import cz.cvut.kbss.jopa.model.annotations.OWLClass;
import cz.cvut.kbss.jopa.model.annotations.OWLDataProperty;
import cz.lukaskabc.ontology.ontopus.core.generated.Vocabulary;

import java.time.Instant;

@OWLClass(iri = Vocabulary.s_c_TemporaryContext)
public class TemporaryContext extends PersistenceEntity {
    @OWLDataProperty(iri = Vocabulary.s_p_org_created_at)
    private Instant createdAt;

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }
}
