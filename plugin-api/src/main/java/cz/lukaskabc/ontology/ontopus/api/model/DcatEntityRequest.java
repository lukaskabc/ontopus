package cz.lukaskabc.ontology.ontopus.api.model;

import cz.lukaskabc.ontology.ontopus.core_model.model.id.GraphURI;
import cz.lukaskabc.ontology.ontopus.core_model.model.id.TypedIdentifier;
import org.springframework.http.MediaType;

/**
 * Request for an internal DCAT entity with {@link #identifier} from {@link #graph} in {@link #mediaType}
 *
 * @param identifier The identifier of the DCAT entity
 * @param graph The database context of the entity
 * @param mediaType The requested media type
 * @param <I> The type of the entity identifier
 */
public record DcatEntityRequest<I extends TypedIdentifier>(I identifier, GraphURI graph, MediaType mediaType) {}
