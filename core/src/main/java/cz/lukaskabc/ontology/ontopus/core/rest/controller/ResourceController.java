package cz.lukaskabc.ontology.ontopus.core.rest.controller;

import cz.lukaskabc.ontology.ontopus.api.rest.StreamingResponseBody;
import cz.lukaskabc.ontology.ontopus.core.rest.utils.StreamingResponseBodyAdapter;
import cz.lukaskabc.ontology.ontopus.core.service.ResourceService;
import cz.lukaskabc.ontology.ontopus.core_model.config.OntopusConfig;
import cz.lukaskabc.ontology.ontopus.core_model.model.id.ResourceURI;
import cz.lukaskabc.ontology.ontopus.core_model.util.VaryHeaderBuilder;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestHeader;

import jakarta.servlet.http.HttpServletRequest;
import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.time.Duration;

@NullMarked
@Controller
public class ResourceController {
    private final ResourceService resourceService;
    private final Duration cacheControlMaxAge;

    public ResourceController(ResourceService resourceService, OntopusConfig config) {
        this.resourceService = resourceService;
        this.cacheControlMaxAge = config.getResource().getCacheControlMaxAge();
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
            @RequestHeader(name = "Accept", required = false) MediaType @Nullable [] requestedTypes,
            HttpServletRequest request) {
        final String decodedUrl = decodeUrl(request);
        final ResourceURI requestedURI = new ResourceURI(decodedUrl);

        final ResponseEntity<? extends StreamingResponseBody> response =
                resourceService.findResource(requestedURI, requestedTypes);
        final StreamingResponseBodyAdapter adaptedBody = adaptBody(response.getBody());

        HttpHeaders headers = response.getHeaders();
        MediaType responseType = headers.getContentType();
        if (responseType == null) {
            responseType = MediaType.TEXT_PLAIN;
        }
        if (responseType.getCharset() == null) {
            responseType = new MediaType(responseType, StandardCharsets.UTF_8);
        }
        headers.setContentType(responseType);

        if (!headers.containsHeader(HttpHeaders.VARY)) {
            VaryHeaderBuilder.withOrigin().addAccept().setToHeaders(headers);
        }

        if (!headers.containsHeader(HttpHeaders.CACHE_CONTROL)) {
            headers.setCacheControl(
                    CacheControl.maxAge(cacheControlMaxAge).cachePublic().mustRevalidate());
        }

        return ResponseEntity.status(response.getStatusCode()).headers(headers).body(adaptedBody);
    }
}
