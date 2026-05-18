package cz.lukaskabc.ontology.ontopus.plugin.dcat_publisher.rest.request;

import cz.lukaskabc.ontology.ontopus.core_model.model.id.TypedIdentifier;
import org.springframework.http.MediaType;

public record DcatEntityRequest<I extends TypedIdentifier>(I identifier, MediaType mediaType) {}
