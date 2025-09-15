package cz.lukaskabc.ontology.ontopus.core.model;

import java.io.Serializable;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

public record SerializableImportContext(List<String> servicesList, List<FormResult> formResults)
        implements Serializable {
    /**
     * @param formData
     * @param reusableFiles
     */
    public record FormResult(Map<String, String[]> formData, Map<String, Path> reusableFiles) implements Serializable {
        // TODO implement factory using relative paths for backed up files available for
        // reusage
    }
}
