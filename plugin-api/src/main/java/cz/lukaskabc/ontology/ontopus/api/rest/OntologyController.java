package cz.lukaskabc.ontology.ontopus.api.rest;

import org.springframework.http.ResponseEntity;

/**
 * Controller capable of handling ontology requests (whole ontology/ontology document).
 *
 * @implSpec Must be registered in Spring context (e.g. with
 *     {@link org.springframework.stereotype.Controller @Controller} annotation)
 * @param <R> the type of the response body,
 *     {@link org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody StreamingResponseBody} is
 *     preferred
 * @see <a href=
 *     "https://docs.spring.io/spring-framework/reference/web/webmvc/mvc-controller/ann-methods/return-types.html">Spring
 *     MVC Return Values</a> (Note that annotations are not supported)
 */
public interface OntologyController<R extends StreamingResponseBody> extends NegotiableController {
    ResponseEntity<R> handleOntologyRequest(OntopusRequest requestContext);
}
