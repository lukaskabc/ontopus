package cz.lukaskabc.ontology.ontopus.core.rest.utils;

import cz.lukaskabc.ontology.ontopus.core.rest.controller.ResourceController;
import cz.lukaskabc.ontology.ontopus.core_model.config.OntopusConfig;
import cz.lukaskabc.ontology.ontopus.core_model.exception.InitializationException;
import org.jspecify.annotations.NonNull;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.condition.CompositeRequestCondition;
import org.springframework.web.servlet.mvc.condition.RequestCondition;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import jakarta.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;

/**
 * Registers {@link ResourceController} as catch all endpoint {@code /**} with {@link RequestUrlStartsWith}.
 *
 * <p>Accepts any URI that does not start with System URI or DCAT Base URI
 */
@Component
public class CatchAllRequestHandlerMapping extends RequestMappingHandlerMapping {
    private final ResourceController resourceController;
    private final OntopusConfig ontopusConfig;

    public CatchAllRequestHandlerMapping(ResourceController resourceController, OntopusConfig ontopusConfig) {
        this.resourceController = resourceController;
        setOrder(1);
        this.ontopusConfig = ontopusConfig;
    }

    @Override
    protected void initHandlerMethods() {
        try {
            Method method =
                    ResourceController.class.getMethod("getResource", MediaType[].class, HttpServletRequest.class);

            final RequestCondition<?> doesNotStartWithSystemUri =
                    new NagatingRequestCondition(new RequestUrlStartsWith(ontopusConfig.getSystemUri()));
            final RequestCondition<?> doesNotStartWithDcatUri = new NagatingRequestCondition(
                    new RequestUrlStartsWith(ontopusConfig.getDcatCatalog().getBaseUri()));
            final RequestCondition<?> combinedConditions =
                    new CompositeRequestCondition(doesNotStartWithSystemUri, doesNotStartWithDcatUri);

            RequestMappingInfo mappingInfo = RequestMappingInfo.paths("/**")
                    .customCondition(combinedConditions)
                    .methods(RequestMethod.GET)
                    .build();

            registerHandlerMethod(resourceController, method, mappingInfo);
        } catch (NoSuchMethodException e) {
            throw new InitializationException("Could not find the fallback controller method", e);
        }
    }

    @Override
    protected boolean isHandler(@NonNull Class<?> beanType) {
        return false;
    }
}
