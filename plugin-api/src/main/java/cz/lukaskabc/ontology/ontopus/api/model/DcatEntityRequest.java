package cz.lukaskabc.ontology.ontopus.api.model;

import cz.lukaskabc.ontology.ontopus.core_model.model.id.GraphURI;
import cz.lukaskabc.ontology.ontopus.core_model.model.id.TypedIdentifier;
import org.springframework.http.MediaType;

import java.net.URI;
import java.util.function.Function;

/**
 * Request for an internal DCAT entity with {@link #identifier} from {@link #graph} in {@link #mediaType}
 *
 * @param identifier The identifier of the DCAT entity
 * @param graph The database context of the entity
 * @param mediaType The requested media type
 * @param <I> The type of the entity identifier
 */
public record DcatEntityRequest<I extends TypedIdentifier>(I identifier, GraphURI graph, MediaType mediaType) {
    public <T extends TypedIdentifier> DcatEntityRequest<T> withTypedIdentifier(
            Function<URI, T> identifierTransformer) {
        return new DcatEntityRequest<>(identifierTransformer.apply(identifier().toURI()), graph(), mediaType());
    }
}
