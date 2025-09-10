package cz.lukaskabc.ontology.ontopus.core.model;

import cz.cvut.kbss.jopa.model.annotations.Id;
import cz.cvut.kbss.jopa.model.annotations.MappedSuperclass;
import jakarta.validation.constraints.NotNull;
import java.net.URI;

/** A persistence (JOPA) entity with an identifier - {@link #uri} */
@MappedSuperclass
public abstract class PersistenceEntity {
    @NotNull @Id(generated = true)
    private URI uri;

    public URI getUri() {
        return uri;
    }

    public void setUri(URI uri) {
        this.uri = uri;
    }
}
