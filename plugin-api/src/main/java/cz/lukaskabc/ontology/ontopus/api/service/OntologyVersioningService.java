package cz.lukaskabc.ontology.ontopus.api.service;

import com.fasterxml.jackson.databind.JsonNode;
import cz.lukaskabc.ontology.ontopus.api.model.FormResult;
import cz.lukaskabc.ontology.ontopus.api.model.ImportProcessContext;
import cz.lukaskabc.ontology.ontopus.core.model.OntologyArtifact;
import org.jspecify.annotations.Nullable;

/**
 * Service capable of versioning an {@link cz.lukaskabc.ontology.ontopus.core.model.OntologyArtifact OntologyArtifact}.
 * The version can be retrieved from a user input or from the ontology data.
 *
 * @implSpec Must be registered in Spring context (e.g. with {@link org.springframework.stereotype.Service @Service}
 *     annotation)
 */
public interface OntologyVersioningService {
    /**
     * Provides form schema shown to the user to enter version data.
     *
     * @return JSON schema
     * @see #getUiSchema()
     * @see <a href= "https://rjsf-team.github.io/react-jsonschema-form/docs/json-schema/">RJSF JSON schema</a>
     * @see <a href= "https://json-schema.org/draft-07/json-schema-release-notes">JSON schema Draft 7</a>
     */
    @Nullable default JsonNode getFormSchema() {
        return null;
    }

    /**
     * Provides UI Schema of the form shown to the user to enter version data.
     *
     * @return UI schema
     * @see #getFormSchema()
     * @see <a href= "https://rjsf-team.github.io/react-jsonschema-form/docs/api-reference/uiSchema">RJSF UI Schema</a>
     */
    @Nullable default JsonNode getUiSchema() {
        return null;
    }

    /**
     * Sets (some) data to partially built ontology artifact.
     *
     * @param formResult The result of the submitted form
     * @param partialOntologyArtifact The object to fill the data with
     * @param context The context of importing process
     * @return The path of the next form to show to the user
     */
    @Nullable String setVersion(
            @Nullable FormResult formResult, OntologyArtifact partialOntologyArtifact, ImportProcessContext context);
}
