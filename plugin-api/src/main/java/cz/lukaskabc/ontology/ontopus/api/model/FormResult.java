package cz.lukaskabc.ontology.ontopus.api.model;

import com.fasterxml.jackson.databind.JsonNode;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.jspecify.annotations.Nullable;

/**
 * Result of submitted form.
 *
 * @param formData Data submitted in the form
 * @param reusableFiles Files submitted along the form or cached on the server mapped to their relative paths
 */
public record FormResult(Map<String, JsonNode> formData, Map<String, ReusableFile> reusableFiles)
        implements Serializable {
    public FormResult(@Nullable Map<String, JsonNode> formData, @Nullable List<ReusableFile> reusableFiles) {
        this(
                formData == null ? Map.of() : formData,
                reusableFiles == null ? Map.of() : mapReusableFiles(reusableFiles));
    }

    public FormResult(Map<String, JsonNode> formData, Map<String, ReusableFile> reusableFiles) {
        this.formData = formData;
        this.reusableFiles = reusableFiles;
    }

    private static Map<String, ReusableFile> mapReusableFiles(List<ReusableFile> reusableFiles) {
        Map<String, ReusableFile> result = new HashMap<>(reusableFiles.size());
        for (ReusableFile file : reusableFiles) {
            result.put(file.getFileName(), file);
        }
        return result;
    }

    /**
     * Resolves a single string value from {@link #formData}.
     *
     * @param key The key to lookup
     * @return The first {@link String} value if present, {@code null} otherwise
     */
    @Nullable public String getStringValue(String key) {
        JsonNode value = formData.get(key);
        if (value != null && value.isTextual()) {
            return value.asText();
        }
        return null; // TODO consider optional
    }
}
