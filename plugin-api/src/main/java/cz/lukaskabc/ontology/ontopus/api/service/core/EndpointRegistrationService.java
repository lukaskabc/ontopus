package cz.lukaskabc.ontology.ontopus.api.service.core;

import cz.lukaskabc.ontology.ontopus.api.model.EndpointRegistrationInfo;

public interface EndpointRegistrationService {
    void register(EndpointRegistrationInfo info);

    void unregister(EndpointRegistrationInfo info); // TODO endpoint unregister
}
