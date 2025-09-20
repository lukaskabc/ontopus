package cz.lukaskabc.ontology.ontopus.api.model;

import com.fasterxml.jackson.databind.JsonNode;
import java.io.Serializable;
import java.util.List;
import java.util.Map;
import org.jspecify.annotations.Nullable;
import org.springframework.util.MultiValueMap;
import org.springframework.web.multipart.MultipartFile;

/**
 * Result of submitted form.
 *
 * @param formData Data submitted in the form
 * @param submittedFiles Files submitted along the form
 */
public record FormResult(Map<String, JsonNode> formData, MultiValueMap<String, MultipartFile> submittedFiles)
        implements Serializable {
    public FormResult(
            @Nullable Map<String, JsonNode> formData, @Nullable MultiValueMap<String, MultipartFile> submittedFiles) {
        this.formData = formData == null ? Map.of() : formData;
        this.submittedFiles = submittedFiles == null ? MultiValueMap.fromSingleValue(Map.of()) : submittedFiles;
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

    /**
     * Resolves a single multipart file from {@link #submittedFiles} associated with the key.
     *
     * @param key The key to lookup
     * @return The {@link List<MultipartFile> List&lt;MultipartFile&gt;} if present, {@code empty list} otherwise
     */
    public List<MultipartFile> getFiles(String key) {
        return submittedFiles.getOrDefault(key, List.of());
    }
}
