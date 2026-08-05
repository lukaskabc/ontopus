package cz.lukaskabc.ontology.ontopus.core_model.model.id;

import java.net.URI;

public interface TypedIdentifier extends Comparable<TypedIdentifier> {
    @Override
    default int compareTo(TypedIdentifier o) {
        return toURI().compareTo(o.toURI());
    }

    URI toURI();
}
