package cz.lukaskabc.ontology.ontopus.api.service.import_process;

import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.impl.LinkedHashModel;

/**
 * Resolves statements to be injected into the original ontology.
 *
 * @implNote the service will be put to the stack automatically and the returned model will be automatically persisted.
 */
public interface OntologyAnnotationInjectionService extends ImportProcessingService<Model> {
    static final Model EMPTY_MODEL = new LinkedHashModel();
}
