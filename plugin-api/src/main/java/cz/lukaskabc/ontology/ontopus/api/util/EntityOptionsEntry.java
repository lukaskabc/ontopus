package cz.lukaskabc.ontology.ontopus.api.util;

import cz.lukaskabc.ontology.ontopus.api.model.FormJsonDataDto;
import cz.lukaskabc.ontology.ontopus.api.model.JsonForm;
import cz.lukaskabc.ontology.ontopus.core_model.model.id.TypedIdentifier;
import org.springframework.util.MultiValueMap;
import org.springframework.web.multipart.MultipartFile;

/** An option menu entry for an artifact */
public interface EntityOptionsEntry<ID extends TypedIdentifier> {
    /**
     * Provides JSON form that will be shown to the user
     *
     * @return the JSON form
     */
    JsonForm getForm(ID entityIdentifier);

    /**
     * A unique identifier of the entry
     *
     * @return the unique identifier
     * @implSpec The identifier should be constant for the entry and should not change between application restarts. The
     *     identifier must be unique.
     */
    default String getIdentifier() {
        return this.getClass().getName();
    }

    /**
     * Label of the menu entry.
     *
     * @return i18n key for the translation of the menu entry.
     */
    String getLabel();

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
