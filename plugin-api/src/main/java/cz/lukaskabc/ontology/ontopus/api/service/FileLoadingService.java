package cz.lukaskabc.ontology.ontopus.api.service;

import com.fasterxml.jackson.databind.JsonNode;
import cz.lukaskabc.ontology.ontopus.api.model.FormResult;
import cz.lukaskabc.ontology.ontopus.api.model.ImportProcessContext;
import org.jspecify.annotations.Nullable;

import java.io.File;

/**
 * Service capable of copying files from a source to a local temporary folder.
 *
 * @implSpec Must be registered in Spring context (e.g. with {@link org.springframework.stereotype.Service @Service}
 * annotation)
 */
public interface FileLoadingService {
    /**
     * Provides form schema shown to the user to enter data required for loading the data.
     *
     * @return JSON schema for the file loading form.
     * @see #getUiSchema()
     * @see <a href= "https://rjsf-team.github.io/react-jsonschema-form/docs/json-schema/">RJSF JSON schema</a>
     * @see <a href= "https://json-schema.org/draft-07/json-schema-release-notes">JSON schema Draft 7</a>
     */
    JsonNode getFormSchema();

    /**
     * Provides UI Schema of the form shown to the user to enter data required for loading the data.
     *
     * @return UI schema for the import form.
     * @see #getFormSchema()
     * @see <a href= "https://rjsf-team.github.io/react-jsonschema-form/docs/api-reference/uiSchema">RJSF UI Schema</a>
     */
    @Nullable
    default JsonNode getUiSchema() {
        return null;
    }

    /**
     * Loads files from the supported source and copies them to a local temporary folder.
     *
     * @param formResult The result of the submitted form
     * @param context    The context of importing process
     * @return the result of the operation
     * @implSpec The caller is responsible for invoking this method asynchronously if necessary.
     */
    Result loadFiles(FormResult formResult, ImportProcessContext context);

    /**
     * Returns the name of file source shown in the UI, so the user knows from where this service is capable of loading
     * the files.
     *
     * @return i18n translation key for the file source.
     */
    String getSourceName();

    /**
     * The result of {@link #loadFiles(FormResult, ImportProcessContext)} operation
     *
     * @param loadedFilesFolder a temporary folder with the loaded files
     * @param nextFormPath      The path of the next form to show to the user
     */
    record Result(File loadedFilesFolder, @Nullable String nextFormPath) {
    }
}
