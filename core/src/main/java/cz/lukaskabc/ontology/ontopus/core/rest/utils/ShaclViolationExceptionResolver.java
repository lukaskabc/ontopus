package cz.lukaskabc.ontology.ontopus.core.rest.utils;

import cz.lukaskabc.ontology.ontopus.core_model.exception.OntopusException;
import cz.lukaskabc.ontology.ontopus.core_model.exception.ValidationException;
import org.eclipse.rdf4j.repository.RepositoryException;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.NestedExceptionUtils;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@NullMarked
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class ShaclViolationExceptionResolver implements HandlerExceptionResolver {
    private final HandlerExceptionResolver handlerExceptionResolver;

    public ShaclViolationExceptionResolver(
            @Qualifier("handlerExceptionResolver") HandlerExceptionResolver springResolver) {
        this.handlerExceptionResolver = springResolver;
    }

    private boolean isShaclViolation(Throwable e) {
        if (e instanceof OntopusException) {
            return false;
        }
        Throwable rootCause = NestedExceptionUtils.getRootCause(e);
        return rootCause instanceof RepositoryException
                && rootCause.getMessage() != null
                && rootCause.getMessage().contains("SHACL Validation Report");
    }

    @Override
    public @Nullable ModelAndView resolveException(
            HttpServletRequest request, HttpServletResponse response, @Nullable Object handler, Exception ex) {
        if (isShaclViolation(ex)) {
            ValidationException translatedEx = ValidationException.builder()
                    .internalMessage("SHACL constraint violation")
                    .detailMessageArguments(OntopusException.EMPTY_ARGUMENTS)
                    .titleMessageCode("ontopus.core.error.invalidData")
                    .detailMessageCode("ontopus.core.error.shaclViolation")
                    .cause(ex)
                    .build();
            return handlerExceptionResolver.resolveException(request, response, handler, translatedEx);
        }

        // not handled, pass it to the next in chain
        return null;
    }
}
