package cz.lukaskabc.ontology.ontopus.api.service.core;

import cz.lukaskabc.ontology.ontopus.api.model.ImportProcessContext;
import cz.lukaskabc.ontology.ontopus.api.service.FileFormatImportingService;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * Service capable of importing provided files into the databse context. Employs {@link FileFormatImportingService
 * FileFormatImportingServices} and attempts to import the given files into the database.
 */
public interface FileToDatabaseImportingService {
    /**
     * Attempts to import provided files.
     *
     * @param filesToImport Files to import
     * @param context The import process context to which files should be imported
     * @return The list of successfully imported files
     */
    List<File> importFiles(List<File> filesToImport, ImportProcessContext context) throws IOException;
}
