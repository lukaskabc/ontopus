package cz.lukaskabc.ontology.ontopus.core.model;

import cz.cvut.kbss.jopa.model.annotations.Id;
import cz.cvut.kbss.jopa.model.annotations.MappedSuperclass;
import cz.lukaskabc.ontology.ontopus.core.model.id.EntityIdentifier;
import jakarta.validation.constraints.NotNull;

/** A persistence (JOPA) entity with an identifier - {@link #uri} */
@MappedSuperclass
public abstract class PersistenceEntity<ID extends EntityIdentifier> {
    @Id
    @NotNull private ID uri;

    public ID getUri() {
        return uri;
    }

    public void setUri(ID uri) {
        this.uri = uri;
    }
}
