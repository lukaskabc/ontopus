package cz.lukaskabc.ontology.ontopus.plugin.widoco.rest;

import cz.lukaskabc.ontology.ontopus.api.rest.OntologyController;
import cz.lukaskabc.ontology.ontopus.api.rest.OntopusRequest;
import cz.lukaskabc.ontology.ontopus.api.rest.ResourceController;
import cz.lukaskabc.ontology.ontopus.api.rest.StreamingResponseBody;
import cz.lukaskabc.ontology.ontopus.core_model.config.OntopusConfig;
import cz.lukaskabc.ontology.ontopus.core_model.util.StringUtils;
import org.jspecify.annotations.NullMarked;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Set;

@NullMarked
@Controller
public class HtmlForwardingController
        implements OntologyController<StreamingResponseBody>, ResourceController<StreamingResponseBody> {
    private static final Set<MediaType> SUPPORTED_MEDIA_TYPES =
            Set.of(MediaType.TEXT_HTML, MediaType.APPLICATION_XHTML_XML);
    private final OntopusConfig ontopusConfig;

    public HtmlForwardingController(OntopusConfig ontopusConfig) {
        this.ontopusConfig = ontopusConfig;
    }

    private UriComponentsBuilder buildRedirectDestination(OntopusRequest requestContext) {
        return UriComponentsBuilder.fromUri(ontopusConfig.getSystemUri())
                .path(WidocoController.PATH)
                .path("/")
                .path(StringUtils.base64EncodeUri(
                        requestContext.ontologyVersionUri().toString()));
    }

    private <T> ResponseEntity<T> found(String destination) {
        return ResponseEntity.status(HttpStatus.FOUND)
                .header("Location", destination)
                .build();
    }

    @Override
    public Set<MediaType> getSupportedMediaTypes() {
        return SUPPORTED_MEDIA_TYPES;
    }

    /** Forwards to {@link WidocoController} which serves the HTML documentation */
    @Override
    public ResponseEntity<StreamingResponseBody> handleOntologyRequest(OntopusRequest requestContext) {
        final String destination = buildRedirectDestination(requestContext).toUriString();
        return found(destination);
    }

    /** Forwards requests for resource to requests of the ontology */
    @Override
    public ResponseEntity<StreamingResponseBody> handleResourceRequest(OntopusRequest requestContext) {
        final String destination = buildRedirectDestination(requestContext)
                .fragment(requestContext.requestedURI().toString())
                .toUriString();
        return found(destination);
    }
}
