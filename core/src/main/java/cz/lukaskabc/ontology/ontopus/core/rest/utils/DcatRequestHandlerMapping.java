package cz.lukaskabc.ontology.ontopus.core.rest.utils;

import cz.lukaskabc.ontology.ontopus.core.rest.controller.PublicDcatController;
import cz.lukaskabc.ontology.ontopus.core.rest.controller.ResourceController;
import cz.lukaskabc.ontology.ontopus.core_model.config.OntopusConfig;
import cz.lukaskabc.ontology.ontopus.core_model.exception.InitializationException;
import org.jspecify.annotations.NonNull;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.condition.RequestCondition;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import jakarta.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;

/** Registers {@link ResourceController} as catch all endpoint {@code /**} with {@link RequestUrlStartsWith}. */
@Component
public class DcatRequestHandlerMapping extends RequestMappingHandlerMapping {
    private final PublicDcatController dcatController;
    private final OntopusConfig ontopusConfig;

    public DcatRequestHandlerMapping(PublicDcatController dcatController, OntopusConfig ontopusConfig) {
        this.dcatController = dcatController;
        setOrder(2);
        this.ontopusConfig = ontopusConfig;
    }

    @Override
    protected void initHandlerMethods() {
        try {
            Method method =
                    PublicDcatController.class.getMethod("getResource", MediaType[].class, HttpServletRequest.class);

            final RequestCondition<?> customCondition = new OrRequestCondition(
                    new RequestUrlStartsWith(ontopusConfig.getDcatCatalog().getBaseUri()),
                    new RequestUrlStartsWith(ontopusConfig.getDcatCatalog().getHttpsBaseUri()));

            RequestMappingInfo mappingInfo = RequestMappingInfo.paths("/**")
                    .customCondition(customCondition)
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
