package cz.lukaskabc.ontology.ontopus.api.rest;

import org.springframework.http.ResponseEntity;

/**
 * Controller capable of handling resource requests (a part of an ontology).
 *
 * @implSpec Must be registered in Spring context (e.g. with
 *     {@link org.springframework.stereotype.Controller @Controller} annotation)
 * @param <R> the type of the response body,
 *     {@link org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody StreamingResponseBody} is
 *     preferred
 * @see <a href=
 *     "https://docs.spring.io/spring-framework/reference/web/webmvc/mvc-controller/ann-methods/return-types.html">Spring
 *     MVC Return Values</a>
 */
public interface ResourceController<R> extends NegotiableController {
    ResponseEntity<R> handleResourceRequest(OntopusRequest requestContext);
}
