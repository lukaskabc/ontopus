package cz.lukaskabc.ontology.ontopus.api.service.core;

import cz.lukaskabc.ontology.ontopus.api.model.ImportProcessContext;

import java.io.File;
import java.io.IOException;
import java.util.List;

/** Service capable of importing provided files into the databse context */
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
