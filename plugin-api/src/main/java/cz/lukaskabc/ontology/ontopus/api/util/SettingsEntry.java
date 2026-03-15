package cz.lukaskabc.ontology.ontopus.api.util;

import cz.lukaskabc.ontology.ontopus.api.model.FormJsonDataDto;
import cz.lukaskabc.ontology.ontopus.api.model.JsonForm;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.multipart.MultipartFile;

/**
 * An entry in settings menu
 *
 * @implSpec Must be registered in Spring context (e.g. with {@link org.springframework.stereotype.Component @Component}
 *     annotation)
 */
@Component
public interface SettingsEntry {

    /**
     * Provides JSON form that will be shown to the user
     *
     * @return the JSON form
     */
    JsonForm getForm();

    /**
     * A unique identifier of the settings entry
     *
     * @return the unique identifier
     * @implSpec The identifier should be constant for the entry and should not change between application restarts.
     */
    default String getIdentifier() {
        return this.getClass().getName();
    }

    /**
     * Label of the settings menu entry.
     *
     * @return i18n key for the translation of the settings menu entry.
     */
    String getLabel();

    /** Handles the data submitted to the form. */
    void handleSubmit(FormJsonDataDto formData, MultiValueMap<String, MultipartFile> files);
}
