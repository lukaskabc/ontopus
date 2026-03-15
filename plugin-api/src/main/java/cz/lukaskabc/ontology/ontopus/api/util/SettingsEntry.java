package cz.lukaskabc.ontology.ontopus.api.util;

import cz.lukaskabc.ontology.ontopus.api.model.FormJsonDataDto;
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
public interface SettingsEntry extends OptionsEntry {

    /** Handles the data submitted to the form. */
    void handleSubmit(FormJsonDataDto formData, MultiValueMap<String, MultipartFile> files);
}
