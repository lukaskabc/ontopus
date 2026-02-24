package cz.lukaskabc.ontology.ontopus.core_model.persistence.identifier;

import cz.lukaskabc.ontology.ontopus.core_model.model.PersistenceEntity;
import cz.lukaskabc.ontology.ontopus.core_model.model.id.TypedIdentifier;
import org.jspecify.annotations.Nullable;

/** An implementation of {@link IdentifierGenerator} that does not generate any identifiers and always returns null. */
public class NoOpIdentifierGenerator<I extends TypedIdentifier, E extends PersistenceEntity<I>>
        implements IdentifierGenerator<I, E> {
    @Nullable @Override
    public I generate(E entity) {
        return null;
    }
}
