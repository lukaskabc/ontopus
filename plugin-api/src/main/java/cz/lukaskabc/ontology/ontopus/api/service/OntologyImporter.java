package cz.lukaskabc.ontology.ontopus.api.service;

import cz.lukaskabc.ontology.ontopus.api.model.Ontology;
import java.io.InputStream;
import org.jspecify.annotations.NullMarked;

/**
 * Object capable of importing a new Ontology from a supported source to the GraphDB
 *
 * @param <I> The type of data object to which result of import form should be deserialized.
 */
@NullMarked
public interface OntologyImporter<I> {
    /**
     * Provides a class to which data from filled import data should be deserialized.
     *
     * @return Class of an object to which data should be deserialized.
     * @see #getImportFormSchema()
     */
    Class<I> getImportFormDataClass();

    /**
     * Provides input form shown to the user to enter data required for importing an ontology.
     *
     * @return Stream of JSON schema for the import form.
     * @see #getImportFormUiSchema()
     * @see #getImportFormDataClass()
     * @see <a href= "https://rjsf-team.github.io/react-jsonschema-form/docs/json-schema/">RJSF JSON schema</a>
     */
    InputStream getImportFormSchema();

    /**
     * Provides UI Schema of the input form shown to the user to enter data required for importing an ontology.
     *
     * @return Stream of UI schema for the import form.
     * @see #getImportFormSchema()
     * @see <a href= "https://rjsf-team.github.io/react-jsonschema-form/docs/api-reference/uiSchema">RJSF UI Schema</a>
     */
    InputStream getImportFormUiSchema();

    /**
     * Returns the name of Ontology source shown in the UI.
     *
     * @return i18n translation key for the Ontology source.
     */
    String getSourceName();

    /**
     * Imports a new ontology version into the database.
     *
     * @param importFormResult The result of import form.
     * @return The imported Ontology saved to the database.
     * @implNote The plugin can access jopa's {@link cz.cvut.kbss.jopa.model.EntityManager EntityManager} with Springs
     *     dependency injection.
     *     <p>Also note that a previous version of the same ontology could already be in the database, so it should be
     *     saved in a unique graph
     */
    Ontology importOntology(I importFormResult);
}
