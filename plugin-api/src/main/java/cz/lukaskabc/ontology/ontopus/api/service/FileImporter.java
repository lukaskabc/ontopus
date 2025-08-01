package cz.lukaskabc.ontology.ontopus.api.service;

import java.io.File;
import java.util.List;
import org.jspecify.annotations.NullMarked;

/**
 * Object capable of importing a local file into GraphDB.
 *
 * @implSpec Must be registered in Spring context (e.g. with {@link org.springframework.stereotype.Component @Component}
 *     annotation)
 */
@NullMarked
public interface FileImporter {
    /**
     * Provides supported file extensions by this importer.
     *
     * @return The list of file extensions.
     */
    List<String> getSupportedFileExtensions();

    /**
     * Imports the provided file into the database.
     *
     * @param file The local file to import.
     * @implSpec The caller is responsible for invoking this method asynchronously.
     */
    void importFile(File file); // TODO add context parameter
}
