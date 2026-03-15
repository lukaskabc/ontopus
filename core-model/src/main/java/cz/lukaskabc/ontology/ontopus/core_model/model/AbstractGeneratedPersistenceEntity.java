package cz.lukaskabc.ontology.ontopus.core_model.model;

import cz.cvut.kbss.jopa.model.annotations.Id;
import cz.cvut.kbss.jopa.model.annotations.MappedSuperclass;
import cz.lukaskabc.ontology.ontopus.core_model.model.id.TypedIdentifier;
import org.jspecify.annotations.Nullable;

import jakarta.validation.constraints.NotNull;
import java.net.URI;

@MappedSuperclass
public abstract class AbstractGeneratedPersistenceEntity<ID extends TypedIdentifier> implements PersistenceEntity<ID> {
    @Id(generated = true)
    private URI uri;

    @Override
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

    @Override
    public void setIdentifier(ID identifier) {
        final URI uri = identifier == null ? null : identifier.toURI();
        setUri(uri);
    }

    protected void setUri(URI uri) {
        this.uri = uri;
    }

    @NotNull protected abstract ID wrapUri(@NotNull URI uri);
}
