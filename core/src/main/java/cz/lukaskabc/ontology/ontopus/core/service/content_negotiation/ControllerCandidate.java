package cz.lukaskabc.ontology.ontopus.core.service.content_negotiation;

import cz.lukaskabc.ontology.ontopus.core_model.model.request_mapping.Controller;
import org.springframework.http.MediaType;

public record ControllerCandidate(MediaType mediaType, Controller controller) {}
