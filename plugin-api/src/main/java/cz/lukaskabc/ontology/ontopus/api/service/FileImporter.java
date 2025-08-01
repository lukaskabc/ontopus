package cz.lukaskabc.ontology.ontopus.api.service;

import java.io.File;
import java.io.IOException;
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
     * Imports the provided files into the database. All the files will have the same format.
     *
     * @param files The files to import
     * @implSpec The caller is responsible for invoking this method asynchronously.
     */
    void importFiles(File[] files) throws IOException; // TODO add context parameter
    // TODO custom exception
}
