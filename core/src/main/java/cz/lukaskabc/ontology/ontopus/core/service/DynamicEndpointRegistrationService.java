package cz.lukaskabc.ontology.ontopus.core.service;

import cz.lukaskabc.ontology.ontopus.api.model.EndpointRegistrationInfo;
import cz.lukaskabc.ontology.ontopus.api.service.core.EndpointRegistrationService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

@Service
public class DynamicEndpointRegistrationService implements EndpointRegistrationService {

    public static RequestMappingInfo getRequestMappingInfo(EndpointRegistrationInfo info) {
        return RequestMappingInfo.paths(info.getPaths())
                .produces(info.getProduces())
                .headers("host=" + info.getHost())
                .methods(RequestMethod.GET)
                .build();
    }

    private final RequestMappingHandlerMapping handlerMapping;

    public DynamicEndpointRegistrationService(
            @Qualifier("requestMappingHandlerMapping") RequestMappingHandlerMapping handlerMapping) {
        this.handlerMapping = handlerMapping;
    }

    @Override
    public void register(EndpointRegistrationInfo info) {
        final RequestMappingInfo mappingInfo = getRequestMappingInfo(info);
        handlerMapping.registerMapping(mappingInfo, info.getHandler(), info.getHandlerMethod());
    }

    @Override
    public void unregister(EndpointRegistrationInfo info) {
        handlerMapping.unregisterMapping(getRequestMappingInfo(info));
    }
}
