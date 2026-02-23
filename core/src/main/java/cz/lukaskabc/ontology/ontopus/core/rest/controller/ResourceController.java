package cz.lukaskabc.ontology.ontopus.core.rest.controller;

import cz.lukaskabc.ontology.ontopus.core_model.model.id.ResourceURI;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.List;

@Controller
public class ResourceController {
    @GetMapping("/**")
    public ResponseEntity<?> getResource(
            @RequestHeader("Accept") MediaType[] requestedTypes, HttpServletRequest request) {
        final ResourceURI requestedURI = new ResourceURI(request.getRequestURL().toString());
        final List<MediaType> sortedTypes = Arrays.asList(requestedTypes);
        MimeTypeUtils.sortBySpecificity(sortedTypes);

        // TODO: call service which will resolve controller based on the type
        // preferences and will resolve the URI mapping and call the controller method
        return ResponseEntity.ok("content");
    }
}
