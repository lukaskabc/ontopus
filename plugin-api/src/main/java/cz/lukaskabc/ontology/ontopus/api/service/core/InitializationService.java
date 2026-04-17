package cz.lukaskabc.ontology.ontopus.api.service.core;

/**
 * Each {@link InitializationService} registered in the Spring context will be executed after singletons are initialized
 * at the startup of the application.
 *
 * <p>The service is removed from the context after execution, there should be no dependants.
 */
public interface InitializationService {
    void initialize();
}
