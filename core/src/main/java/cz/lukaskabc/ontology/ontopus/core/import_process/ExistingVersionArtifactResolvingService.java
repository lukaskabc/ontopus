package cz.lukaskabc.ontology.ontopus.core.import_process;

import cz.lukaskabc.ontology.ontopus.api.model.ImportProcessContext;
import cz.lukaskabc.ontology.ontopus.api.model.JsonForm;
import cz.lukaskabc.ontology.ontopus.api.model.ReadOnlyImportProcessContext;
import cz.lukaskabc.ontology.ontopus.api.service.import_process.ImportProcessingService;
import cz.lukaskabc.ontology.ontopus.core_model.exception.JsonFormSubmitException;
import cz.lukaskabc.ontology.ontopus.core_model.generated.Vocabulary;
import cz.lukaskabc.ontology.ontopus.core_model.model.id.OntologyVersionURI;
import cz.lukaskabc.ontology.ontopus.core_model.model.ontology.VersionArtifact;
import cz.lukaskabc.ontology.ontopus.core_model.model.util.FormResult;
import cz.lukaskabc.ontology.ontopus.core_model.service.VersionArtifactService;
import org.jspecify.annotations.Nullable;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.node.ObjectNode;

import java.util.Objects;

/**
 * Checks whether there is an existing {@link cz.lukaskabc.ontology.ontopus.core_model.model.ontology.VersionArtifact
 * VersionArtifact} for the given ontology version
 */
public class ExistingVersionArtifactResolvingService implements ImportProcessingService<Void> {
    private static final String TRANSLATION_ROOT =
            "ontopus.core.service.ImportProcessingService.ExistingVersionArtifactResolvingService";

    private static JsonForm makeForm(ObjectMapper objectMapper) {
        ObjectNode jsonScheme = objectMapper.createObjectNode();
        ObjectNode uiScheme = objectMapper.createObjectNode();
        uiScheme.putObject("ui:globalOptions").put("enableMarkdownInDescription", true);
        jsonScheme.put("type", "object").put("$translationRoot", TRANSLATION_ROOT);
        return new JsonForm(jsonScheme, uiScheme, null);
    }

    private final VersionArtifactService versionArtifactService;

    private final JsonForm jsonForm;

    @Nullable private VersionArtifact existingArtifact;

    public ExistingVersionArtifactResolvingService(
            VersionArtifactService versionArtifactService, ObjectMapper objectMapper) {
        this.versionArtifactService = versionArtifactService;
        this.jsonForm = makeForm(objectMapper);
    }

    @Override
    public void afterStackPush(ImportProcessContext context) {
        findExisting(context);
        if (existingArtifact != null
                && !context.getVersionSeries().getIdentifier().equals(existingArtifact.getSeries())) {
            context.pushService(new ErrorThrowingService(JsonFormSubmitException.builder()
                    .errorType(Vocabulary.u_i_already_exists)
                    .internalMessage("Unable to publish existing version as a new ontology")
                    .titleMessageCode("ontopus.core.error.ontologyExists")
                    .detailMessageArguments(
                            new Object[] {context.getVersionSeries().getIdentifier(), existingArtifact.getVersionUri()})
                    .detailMessageCode("ontopus.core.error.versionSeriesMismatch")
                    .build()));
        }
    }

    private void findExisting(ImportProcessContext context) {
        final String version = context.getVersionArtifact().getVersion();
        final OntologyVersionURI versionURI = context.getVersionArtifact().getVersionUri();
        Objects.requireNonNull(version, "Version of the version artifact must not be null");
        Objects.requireNonNull(versionURI, "Version URI of the version artifact must not be null");
        this.existingArtifact =
                versionArtifactService.findByVersionUri(versionURI).orElse(null);
    }

    @Override
    public @Nullable JsonForm getJsonForm(ReadOnlyImportProcessContext context, @Nullable JsonNode previousFormData) {
        if (existingArtifact == null || context.isNonInteractive()) {
            return null;
        }
        return this.jsonForm;
    }

    @Override
    public String getServiceName() {
        return TRANSLATION_ROOT + ".title";
    }

    @Override
    public Void handleSubmit(FormResult formResult, ImportProcessContext context) throws JsonFormSubmitException {
        if (existingArtifact == null) {
            return null;
        }
        final VersionArtifact newArtifact = context.getVersionArtifact();
        Objects.requireNonNull(existingArtifact);
        newArtifact.setIdentifier(existingArtifact.getIdentifier());
        newArtifact.setVersion(existingArtifact.getVersion());
        newArtifact.setVersionUri(existingArtifact.getVersionUri());
        newArtifact.setSeries(existingArtifact.getSeries());
        newArtifact.setReleaseDate(existingArtifact.getReleaseDate());
        return null;
    }
}
