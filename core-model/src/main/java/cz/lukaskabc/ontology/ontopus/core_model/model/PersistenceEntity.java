package cz.lukaskabc.ontology.ontopus.core_model.model;

import cz.cvut.kbss.jopa.model.annotations.Id;
import cz.cvut.kbss.jopa.model.annotations.MappedSuperclass;
import cz.lukaskabc.ontology.ontopus.core_model.model.id.EntityIdentifier;
import jakarta.validation.constraints.NotNull;
import java.net.URI;

/** A persistence (JOPA) entity with an identifier - {@link #uri} */
@MappedSuperclass
public abstract class PersistenceEntity<ID extends EntityIdentifier> {
    @Id
    @NotNull private URI uri;

    public abstract ID getIdentifier();

    public URI getUri() {
        return uri;
    }

    public abstract void setIdentifier(ID identifier);

    public void setUri(URI uri) {
        this.uri = uri;
    }
}
