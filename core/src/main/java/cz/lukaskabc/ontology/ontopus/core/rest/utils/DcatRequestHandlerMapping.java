package cz.lukaskabc.ontology.ontopus.core.rest.utils;

import cz.lukaskabc.ontology.ontopus.core.rest.controller.PublicDcatController;
import cz.lukaskabc.ontology.ontopus.core.rest.controller.ResourceController;
import cz.lukaskabc.ontology.ontopus.core_model.config.OntopusConfig;
import cz.lukaskabc.ontology.ontopus.core_model.exception.InitializationException;
import org.jspecify.annotations.NonNull;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import jakarta.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.net.URI;

/**
 * Registers {@link ResourceController} as catch all endpoint {@code /**} with {@link RequestUrlNotStartsWithCondition}.
 */
@Component
public class DcatRequestHandlerMapping extends RequestMappingHandlerMapping {
    private final PublicDcatController dcatController;
    private final OntopusConfig ontopusConfig;

    public DcatRequestHandlerMapping(PublicDcatController dcatController, OntopusConfig ontopusConfig) {
        this.dcatController = dcatController;
        setOrder(1);
        this.ontopusConfig = ontopusConfig;
    }

    @Override
    protected void initHandlerMethods() {
        try {
            Method method =
                    PublicDcatController.class.getMethod("getResource", MediaType[].class, HttpServletRequest.class);

            final URI baseUri = ontopusConfig.getDcatCatalog().getBaseUri();

            RequestMappingInfo mappingInfo = RequestMappingInfo.paths("/**")
                    .customCondition(new cz.lukaskabc.ontology.ontopus.core.rest.utils.NotRequestCondition(
                            new RequestUrlNotStartsWithCondition(baseUri)))
                    .methods(RequestMethod.GET)
                    .build();

            registerHandlerMethod(dcatController, method, mappingInfo);
        } catch (NoSuchMethodException e) {
            throw new InitializationException("Could not find the fallback controller method", e);
        }
    }

    @Override
    protected boolean isHandler(@NonNull Class<?> beanType) {
        return false;
    }
}
