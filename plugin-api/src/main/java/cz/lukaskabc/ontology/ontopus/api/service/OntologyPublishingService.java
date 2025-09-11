package cz.lukaskabc.ontology.ontopus.api.service;

import com.fasterxml.jackson.databind.JsonNode;
import cz.lukaskabc.ontology.ontopus.api.model.FormResult;
import cz.lukaskabc.ontology.ontopus.api.model.ImportProcessContext;
import cz.lukaskabc.ontology.ontopus.core.model.OntologyArtifact;
import org.jspecify.annotations.Nullable;

/**
 * Service capable of publishing an {@link cz.lukaskabc.ontology.ontopus.core.model.OntologyArtifact OntologyArtifact}
 * via a public endpoint.
 * <p>
 * The service is triggered once {@link cz.lukaskabc.ontology.ontopus.core.model.OntologyArtifact OntologyArtifact}
 * is fully constructed. The service can request input from user and do any necessary pre-processing in order to publish the artifact (e.g. generating static files).
 */
public interface OntologyPublishingService {
    /**
     * Provides form schema shown to the user to enter data required for publishing the artifact.
     *
     * @return JSON schema
     * @see #getUiSchema()
     * @see <a href= "https://rjsf-team.github.io/react-jsonschema-form/docs/json-schema/">RJSF JSON schema</a>
     * @see <a href= "https://json-schema.org/draft-07/json-schema-release-notes">JSON schema Draft 7</a>
     */
    @Nullable
    default JsonNode getFormSchema() {
        return null;
    }

    /**
     * Provides UI Schema of the form shown to the user to enter data required for publishing the artifact.
     *
     * @return UI schema
     * @see #getFormSchema()
     * @see <a href= "https://rjsf-team.github.io/react-jsonschema-form/docs/api-reference/uiSchema">RJSF UI Schema</a>
     */
    @Nullable
    default JsonNode getUiSchema() {
        return null;
    }

    /**
     * Sets (some) data to partially built ontology artifact.
     *
     * @param formResult       The result of the submitted form
     * @param ontologyArtifact The ontology artifact to publish
     * @param context          The context of importing process
     * @return The path of the next form to show to the user
     * @implSpec The caller is responsible for invoking this method asynchronously if necessary.
     */
    @Nullable
    String publish(
        @Nullable FormResult formResult, OntologyArtifact ontologyArtifact, ImportProcessContext context);
}
