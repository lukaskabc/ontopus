package cz.lukaskabc.ontology.ontopus.core_model.model.util;

import org.jspecify.annotations.Nullable;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.node.ObjectNode;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Result of submitted form.
 *
 * @param formData Data submitted in the form
 * @param uploadedFiles Files submitted along the form mapped to their relative paths
 */
public record FormResult(Map<String, JsonNode> formData, Map<String, UploadedFile> uploadedFiles)
        implements Serializable {
    public FormResult(@Nullable Map<String, JsonNode> formData, @Nullable List<UploadedFile> uploadedFiles) {
        this(
                formData == null ? Map.of() : formData,
                uploadedFiles == null ? Map.of() : mapUploadedFiles(uploadedFiles));
    }

    private static Map<String, UploadedFile> mapUploadedFiles(List<UploadedFile> uploadedFiles) {
        Map<String, UploadedFile> result = new HashMap<>(uploadedFiles.size());
        for (UploadedFile file : uploadedFiles) {
            result.put(file.path(), file);
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
        if (value != null && value.isString()) {
            return value.asString();
        }
        return null;
    }

    /**
     * Resolves a single {@link UploadedFile} from {@link #uploadedFiles}.
     *
     * @param relativePath the relative path of the file to lookup
     * @return The {@link UploadedFile} if present, {@code null} otherwise
     */
    @Nullable public UploadedFile getUploadedFile(String relativePath) {
        return uploadedFiles.get(relativePath);
    }

    public ObjectNode jsonFormData(ObjectMapper objectMapper) {
        final ObjectNode result = objectMapper.createObjectNode();
        for (Map.Entry<String, JsonNode> entry : formData.entrySet()) {
            result.set(entry.getKey(), entry.getValue());
        }
        return result;
    }
}
