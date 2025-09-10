package cz.lukaskabc.ontology.ontopus.api.model;

import java.util.Map;
import org.springframework.web.multipart.MultipartFile;

/**
 * Result of submitted form.
 *
 * @param formData Data submitted in the form
 * @param submittedFiles Files submitted along the form
 */
public record FormResult(Map<String, String[]> formData, Map<String, MultipartFile> submittedFiles) {}
