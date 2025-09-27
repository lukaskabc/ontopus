package cz.lukaskabc.ontology.ontopus.api.service;

import cz.lukaskabc.ontology.ontopus.api.model.FormResult;
import cz.lukaskabc.ontology.ontopus.api.model.ImportProcessContext;
import java.nio.file.Path;
// TODO rename, first step is to load ontology into the database not explicitly stating files
// file service should handle file copy and then database import

/**
 * Service capable of copying files from a source to a local temporary folder.
 *
 * @implSpec Must be registered in Spring context (e.g. with {@link org.springframework.stereotype.Service @Service}
 *     annotation)
 */
public interface FileLoadingService extends ImportProcessingService<Path> {

    /**
     * Provides the name of the file source shown in the UI. This indicates from where the user will load the files.
     *
     * @return i18n translation key for the file source
     */
    @Override
    String getServiceName();

    /**
     * Loads files from the supported source and copies them to a local temporary folder.
     *
     * @param formResult The result of the submitted form
     * @param context The context of importing process
     * @return the result of the operation with path to the created folder
     * @implSpec The caller is responsible for invoking this method asynchronously if blocking operation is not desired.
     */
    @Override
    Result<Path> handleSubmit(FormResult formResult, ImportProcessContext context);
}
