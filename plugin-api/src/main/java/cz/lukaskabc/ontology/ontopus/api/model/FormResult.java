package cz.lukaskabc.ontology.ontopus.api.model;

import java.io.Serializable;
import java.util.Map;
import org.jspecify.annotations.Nullable;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

/**
 * Result of submitted form.
 *
 * @param formData Data submitted in the form
 * @param submittedFiles Files submitted along the form
 */
public record FormResult(Map<String, String[]> formData, Map<String, MultipartFile> submittedFiles)
        implements Serializable {
    public FormResult(MultipartHttpServletRequest request) {
        this(request.getParameterMap(), request.getFileMap());
    }
    /**
     * Resolves a single string value from {@link #formData}.
     *
     * @param key The key to lookup
     * @return The first {@link String} value if present, {@code null} otherwise
     */
    @Nullable public String getStringValue(String key) {
        String[] values = formData.get(key);
        if (values != null && values.length > 0) {
            return values[0];
        }
        return null;
    }

    /**
     * Resolves a single multipart file from {@link #submittedFiles} associated with the key.
     *
     * @param key The key to lookup
     * @return The {@link MultipartFile} if present, {@code null} otherwise
     */
    @Nullable public MultipartFile getMultipartFile(String key) {
        if (submittedFiles != null) {
            return submittedFiles.get(key);
        }
        return null;
    }
}
