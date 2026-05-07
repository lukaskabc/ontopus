package cz.lukaskabc.ontology.ontopus.api.service;

import cz.lukaskabc.ontology.ontopus.api.model.ImportProcessContext;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * Service capable of importing data from local files of a specific format(s) into a database context
 *
 * @implSpec Must be registered in Spring context (e.g. with {@link org.springframework.stereotype.Service @Service}
 *     annotation)
 */
public interface FileFormatImportingService {

    /**
     * Imports the provided files into the database. If the file contains prefix declarations, it adds them to the
     * prefix declarations of the {@link cz.lukaskabc.ontology.ontopus.core_model.model.ontology.VersionArtifact
     * VersionArtifact}.
     *
     * @param files The files to import for which {@link #supports(File)} returned true
     * @param context The context of importing process
     * @implSpec The caller is responsible for invoking this method asynchronously if necessary.
     */
    void importFiles(List<File> files, ImportProcessContext context) throws IOException;

    /**
     * Checks whether the service is able to import the format of the specified file.
     *
     * @param file the file of which format to check
     * @return {@code true} when the importer is able to import the file, {@code false} otherwise
     */
    boolean supports(File file);
}
