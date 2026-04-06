package cz.lukaskabc.ontology.ontopus.core.rest.controller;

import cz.lukaskabc.ontology.ontopus.api.rest.StreamingResponseBody;
import cz.lukaskabc.ontology.ontopus.core.rest.utils.StreamingResponseBodyAdapter;
import cz.lukaskabc.ontology.ontopus.core.service.ResourceService;
import cz.lukaskabc.ontology.ontopus.core_model.model.id.ResourceURI;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestHeader;

import jakarta.servlet.http.HttpServletRequest;
import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

@NullMarked
@Controller
public class ResourceController {
    private final ResourceService resourceService;

    public ResourceController(ResourceService resourceService) {
        this.resourceService = resourceService;
    }

    @Nullable private StreamingResponseBodyAdapter adaptBody(@Nullable StreamingResponseBody body) {
        if (body == null) {
            return null;
        }
        return new StreamingResponseBodyAdapter(body);
    }

    private String decodeUrl(HttpServletRequest request) {
        final Charset charset = getCharset(request);
        return URLDecoder.decode(request.getRequestURL().toString(), charset);
    }

    private Charset getCharset(HttpServletRequest request) {
        String encoding = request.getCharacterEncoding();
        if (encoding != null) {
            try {
                return Charset.forName(encoding);
            } catch (Exception e) {
                // fallback to UTF-8
            }
        }
        return StandardCharsets.UTF_8;
    }

    public ResponseEntity<StreamingResponseBodyAdapter> getResource(
            @RequestHeader("Accept") MediaType[] requestedTypes, HttpServletRequest request) {
        final String decodedUrl = decodeUrl(request);
        final ResourceURI requestedURI = new ResourceURI(decodedUrl);
        // TODO does not account for leading slash
        // e.g. http://purl.org/dc/terms/.ttl works
        // http://purl.org/dc/terms.ttl does not

        final ResponseEntity<StreamingResponseBody> response =
                resourceService.getResource(requestedURI, requestedTypes);
        final StreamingResponseBodyAdapter adaptedBody = adaptBody(response.getBody());

        return ResponseEntity.status(response.getStatusCode())
                .headers(response.getHeaders())
                .body(adaptedBody);
    }
}
