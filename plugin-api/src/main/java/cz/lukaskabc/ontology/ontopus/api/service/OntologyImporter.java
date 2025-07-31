package cz.lukaskabc.ontology.ontopus.api.service;

import com.fasterxml.jackson.databind.JsonNode;
import java.util.Map;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import org.springframework.web.multipart.MultipartFile;

/**
 * Object capable of importing a new Ontology from a supported source to the GraphDB.
 *
 * @implSpec Must be registered in Spring context (e.g. with {@link org.springframework.stereotype.Component @Component}
 *     annotation)
 */
@NullMarked
public interface OntologyImporter {

    /**
     * Provides input form shown to the user to enter data required for importing an ontology.
     *
     * @return JSON schema for the import form.
     * @see #getImportFormUiSchema()
     * @see <a href= "https://rjsf-team.github.io/react-jsonschema-form/docs/json-schema/">RJSF JSON schema</a>
     * @see <a href= "https://json-schema.org/draft-07/json-schema-release-notes">JSON schema Draft 7</a>
     */
    JsonNode getImportFormSchema();

    /**
     * Provides UI Schema of the input form shown to the user to enter data required for importing an ontology.
     *
     * @return UI schema for the import form.
     * @see #getImportFormSchema()
     * @see <a href= "https://rjsf-team.github.io/react-jsonschema-form/docs/api-reference/uiSchema">RJSF UI Schema</a>
     */
    @Nullable default JsonNode getImportFormUiSchema() {
        return null;
    }

    /**
     * Provides the path of a rest endpoint from this plugin returning next
     * {@link cz.lukaskabc.ontology.ontopus.api.model.StagedJsonForm StagedJsonForm}.
     *
     * @return absolute path matching
     *     {@link cz.lukaskabc.ontology.ontopus.api.model.StagedJsonForm#ABSOLUTE_REST_PATH_REGEX ABSOLUTE_REST_PATH}
     *     regex pattern of the rest endpoint.
     */
    @Nullable default String getNextImportFormPath() {
        return null;
    }

    /**
     * Returns the name of Ontology source shown in the UI.
     *
     * @return i18n translation key for the Ontology source.
     */
    String getSourceName();

    /**
     * Imports a new ontology version into the database. The method is executed asynchronously by the core and should
     * publish {@link cz.lukaskabc.ontology.ontopus.api.event.OntologyImportFinished OntologyImportFinished} event once
     * the import is finished.
     *
     * @param formData The result of import form.
     * @implNote The plugin can access jopa's {@link cz.cvut.kbss.jopa.model.EntityManager EntityManager} with Springs
     *     dependency injection.
     *     <p>Also note that a previous version of the same ontology could already be in the database, so it should be
     *     saved in a unique graph
     */
    void importOntology(Map<String, String[]> formData, Map<String, MultipartFile> files);
}
