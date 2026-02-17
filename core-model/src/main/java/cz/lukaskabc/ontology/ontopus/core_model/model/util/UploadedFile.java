package cz.lukaskabc.ontology.ontopus.core_model.model.util;

import java.nio.file.Path;

/**
 * Represents a file that has been uploaded and is stored at a specific path on the filesystem. The stored file will
 * have a different name than the original file.
 *
 * @param originalName the original name of the uploaded file, as provided by the user
 * @param path the path on the filesystem where the uploaded file is stored
 */
public record UploadedFile(String originalName, Path path) {}
