package cz.lukaskabc.ontology.ontopus.api.util;

import cz.lukaskabc.ontology.ontopus.api.model.FormJsonDataDto;
import cz.lukaskabc.ontology.ontopus.core_model.model.id.TypedIdentifier;
import org.springframework.util.MultiValueMap;
import org.springframework.web.multipart.MultipartFile;

/** An option menu entry for an artifact */
public interface EntityOptionsEntry<ID extends TypedIdentifier> extends OptionsEntry {
    /** Handles the data submitted to the form. */
    void handleSubmit(ID artifactIdentifier, FormJsonDataDto formData, MultiValueMap<String, MultipartFile> files);

    /**
     * Checks whether the menu option should be shown for the specified artifact
     *
     * @param artifactIdentifier the artifact identifier
     * @return true when the option should be shown for the artifact, false otherwise
     */
    boolean showMenuEntry(ID artifactIdentifier);
}
