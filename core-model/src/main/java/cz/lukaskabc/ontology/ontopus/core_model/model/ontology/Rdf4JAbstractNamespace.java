package cz.lukaskabc.ontology.ontopus.core_model.model.ontology;

import cz.cvut.kbss.jopa.model.annotations.MappedSuperclass;
import cz.lukaskabc.ontology.ontopus.core_model.model.AbstractGeneratedPersistenceEntity;
import cz.lukaskabc.ontology.ontopus.core_model.model.id.Rdf4JAbstractNamespaceURI;
import org.eclipse.rdf4j.model.Namespace;
import org.eclipse.rdf4j.model.base.AbstractNamespace;
import org.jspecify.annotations.NullUnmarked;
import org.jspecify.annotations.Nullable;

import java.net.URI;
import java.util.Comparator;
import java.util.Objects;

/** @see AbstractNamespace */
@MappedSuperclass
@NullUnmarked
public abstract class Rdf4JAbstractNamespace extends AbstractGeneratedPersistenceEntity<Rdf4JAbstractNamespaceURI>
        implements Namespace {
    /**
     * Sorts namespaces first by {@linkplain #getPrefix() prefix} and then by {@linkplain #getName()} () name};
     * {@code null} values are sorted before other values.
     */
    private static final Comparator<Namespace> COMPARATOR =
            Comparator.nullsFirst(Comparator.comparing(Namespace::getPrefix).thenComparing(Namespace::getName));

    @Override
    public int compareTo(@Nullable Namespace o) {
        return COMPARATOR.compare(this, o);
    }

    @Override
    public boolean equals(@Nullable Object object) {
        return this == object
                || object instanceof Namespace
                        && getPrefix().equals(((Namespace) object).getPrefix())
                        && getName().equals(((Namespace) object).getName());
    }

    @Override
    public int hashCode() { // TODO inline Objects.hash() to avoid array creation?
        return Objects.hash(getPrefix(), getName());
    }

    @Override
    public String toString() {
        return getPrefix() + " :: " + getName();
    }

    @Override
    protected Rdf4JAbstractNamespaceURI wrapUri(URI uri) {
        return new Rdf4JAbstractNamespaceURI(uri);
    }
}
