package cz.lukaskabc.ontology.ontopus.core.service.process;

import cz.lukaskabc.ontology.ontopus.api.model.ImportProcessContext;
import cz.lukaskabc.ontology.ontopus.api.model.JsonForm;
import cz.lukaskabc.ontology.ontopus.api.model.ReadOnlyImportProcessContext;
import cz.lukaskabc.ontology.ontopus.api.service.import_process.ImportProcessingService;
import cz.lukaskabc.ontology.ontopus.api.service.import_process.OntologyVersioningService;
import cz.lukaskabc.ontology.ontopus.core.exception.VersionURIConstructionException;
import cz.lukaskabc.ontology.ontopus.core_model.exception.JsonFormSubmitException;
import cz.lukaskabc.ontology.ontopus.core_model.exception.OntopusException;
import cz.lukaskabc.ontology.ontopus.core_model.model.id.OntologyURI;
import cz.lukaskabc.ontology.ontopus.core_model.model.id.OntologyVersionURI;
import cz.lukaskabc.ontology.ontopus.core_model.model.id.VersionSeriesURI;
import cz.lukaskabc.ontology.ontopus.core_model.model.util.FormResult;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.util.UriComponentsBuilder;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.node.ObjectNode;

import java.net.URI;

/**
 * {@link OntologyVersioningService} that constructs the version URI by concatenating ontology URI and version. Uses
 * {@code VersionUriField} on the frontend to allow the user to customize the URI template.
 */
@NullMarked
@Service
public class VersionURIConstructionService implements ImportProcessingService<OntologyVersionURI> {
    static final String VERSION_SEGMENT = "{version}";
    static final String URI_FIELD_NAME = "uri";
    static final String VERSION_FIELD_NAME = "version";

    private static JsonForm makeForm(ObjectMapper objectMapper) {
        ObjectNode scheme = objectMapper.createObjectNode();
        scheme.put("type", "object");
        ObjectNode properties = scheme.putObject("properties");
        properties.putObject(URI_FIELD_NAME).put("type", "string");
        properties.putObject(VERSION_FIELD_NAME).put("type", "string");

        ObjectNode uiSchema = objectMapper.createObjectNode();
        uiSchema.put("ui:field", "versionUriField");

        return new JsonForm(scheme, uiSchema, null);
    }

    private final ObjectMapper objectMapper;

    private final JsonForm jsonForm;

    public VersionURIConstructionService(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
        this.jsonForm = makeForm(objectMapper);
    }

    @Override
    public @Nullable JsonForm getJsonForm(ReadOnlyImportProcessContext context, @Nullable JsonNode previousFormData) {
        final String version = version(context);
        final String uri = UriComponentsBuilder.fromUri(ontologyURI(context).toURI())
                .pathSegment(VERSION_SEGMENT)
                .build()
                .toUriString();

        ObjectNode formData = objectMapper.createObjectNode();

        if (previousFormData != null && previousFormData.isObject()) {
            formData.setAll((ObjectNode) previousFormData);
        } else {
            formData.put(URI_FIELD_NAME, uri);
        }
        // always set new version
        formData.put(VERSION_FIELD_NAME, version);

        return this.jsonForm.withFormData(formData);
    }

    @Override
    public String getServiceName() {
        return this.getClass().getSimpleName();
    }

    @Override
    public OntologyVersionURI handleSubmit(FormResult formResult, ImportProcessContext context) {
        final String version = version(formResult);
        final URI versionURI = versionURI(formResult).buildAndExpand(version).toUri();
        return new OntologyVersionURI(versionURI);
    }

    /**
     * Extracts the {@link cz.lukaskabc.ontology.ontopus.core_model.model.ontology.VersionSeries} identifier from the
     * context
     *
     * @param context the import process context containing the version series information
     * @return the version series identifier as a {@link VersionSeriesURI}
     * @throws VersionURIConstructionException if the version series identifier is missing in the context
     */
    private OntologyURI ontologyURI(ReadOnlyImportProcessContext context) {
        OntologyURI ontologyIdentifier = context.getVersionSeries().getOntologyURI();
        if (ontologyIdentifier == null) {
            throw VersionURIConstructionException.builder()
                    .internalMessage("Failed to construct version URI, "
                            + "the ontology identifier is missing for the version series: "
                            + context.getVersionSeries())
                    .detailMessageArguments(OntopusException.EMPTY_ARGUMENTS)
                    .detailMessageCode("ontopus.core.error.versionURI.missingOntologyURI")
                    .titleMessageCode("ontopus.core.error.versionURI.failed")
                    .build();
        }
        return ontologyIdentifier;
    }

    private String version(FormResult formResult) {
        final String version = formResult.getStringValue(VERSION_FIELD_NAME);
        if (!StringUtils.hasText(version)) {
            throw JsonFormSubmitException.missingValue("ontology version");
        }
        return version;
    }

    /**
     * Extracts the version from the {@link cz.lukaskabc.ontology.ontopus.core_model.model.ontology.VersionArtifact
     * VersionArtifact} in the context.
     *
     * @param context the import process context containing the version artifact
     * @return URL encoded version
     */
    private String version(ReadOnlyImportProcessContext context) {
        String version = context.getVersionArtifact().getVersion();
        if (!StringUtils.hasText(version)) {
            throw VersionURIConstructionException.builder()
                    .internalMessage("Missing ontology version for version artifact: " + context.getVersionArtifact())
                    .detailMessageArguments(OntopusException.EMPTY_ARGUMENTS)
                    .detailMessageCode("ontopus.core.error.versionURI.missingVersion")
                    .titleMessageCode("ontopus.core.error.versionURI.failed")
                    .build();
        }

        return UriComponentsBuilder.fromPath(version).build().toUri().toString();
    }

    private UriComponentsBuilder versionURI(FormResult formResult) {
        final String uri = formResult.getStringValue(URI_FIELD_NAME);
        if (!StringUtils.hasText(uri)) {
            throw JsonFormSubmitException.missingValue("ontology URI");
        }
        return UriComponentsBuilder.fromUriString(uri);
    }
}
