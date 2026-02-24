package cz.lukaskabc.ontology.ontopus.core_model.model;

import cz.cvut.kbss.jopa.model.annotations.Id;
import cz.cvut.kbss.jopa.model.annotations.MappedSuperclass;
import cz.lukaskabc.ontology.ontopus.core_model.model.id.TypedIdentifier;
import org.jspecify.annotations.Nullable;

import jakarta.validation.constraints.NotNull;
import java.net.URI;

/** A persistence (JOPA) entity with an identifier - {@link #uri} */
@MappedSuperclass
public abstract class PersistenceEntity<ID extends TypedIdentifier> {
    @Id
    @NotNull private URI uri;

    @Nullable public ID getIdentifier() {
        final URI uri = getUri();
        if (uri == null) {
            return null;
        }
        return wrapUri(uri);
    }

    protected URI getUri() {
        return uri;
    }

    public void setIdentifier(ID identifier) {
        setUri(identifier.toURI());
    }

    protected void setUri(URI uri) {
        this.uri = uri;
    }

    @NotNull protected abstract ID wrapUri(@NotNull URI uri);
}
