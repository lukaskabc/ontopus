package cz.lukaskabc.ontology.ontopus.api.service;

import cz.lukaskabc.ontology.ontopus.api.model.FormResult;
import cz.lukaskabc.ontology.ontopus.api.model.ImportProcessContext;

/**
 * Service capable of loading an ontology from a remote source into the database.
 *
 * <p>The loading process may consist of several steps, e.g. copying files and then loading them to the database.
 *
 * @implSpec Must be registered in Spring context (e.g. with {@link org.springframework.stereotype.Service @Service}
 *     annotation)
 */
public interface OntologyLoadingService extends ImportProcessingService<Void> {

    /**
     * Provides the name of the ontology source shown in the UI. This indicates from where the user will load the data.
     *
     * @return i18n translation key for the file source
     */
    @Override
    String getServiceName();

    /**
     * Loads the data of the ontology into the temporary graph of the import process context.
     *
     * @param formResult The result of the submitted form
     * @param context The context of importing process
     * @return the path to the created folder
     * @implSpec The caller is responsible for invoking this method asynchronously if blocking operation is not desired.
     */
    @Override
    Void handleSubmit(FormResult formResult, ImportProcessContext context);
}
