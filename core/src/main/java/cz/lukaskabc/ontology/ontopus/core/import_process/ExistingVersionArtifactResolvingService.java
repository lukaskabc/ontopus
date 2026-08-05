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
import cz.lukaskabc.ontology.ontopus.core_model.util.EntityMapper;
import org.jspecify.annotations.Nullable;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.node.ObjectNode;

import java.util.Objects;

/** Checks whether there is an existing {@link VersionArtifact} for the given ontology version */
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

    private final EntityMapper entityMapper;

    @Nullable private VersionArtifact existingArtifact;

    public ExistingVersionArtifactResolvingService(
            VersionArtifactService versionArtifactService, ObjectMapper objectMapper, EntityMapper entityMapper) {
        this.versionArtifactService = versionArtifactService;
        this.jsonForm = makeForm(objectMapper);
        this.entityMapper = entityMapper;
    }

    /**
     * Check that {@link ImportProcessContext} contains a version artifact that is already persisted in the databse. If
     * an existing artifact is found check that it belongs to the
     * {@link cz.lukaskabc.ontology.ontopus.core_model.model.ontology.VersionSeries VersionSeries} matching the current
     * context.
     *
     * @param context The process context with service stack with this service at the top.
     */
    @Override
    public void afterStackPush(ImportProcessContext context) {
        this.existingArtifact = findExisting(context);
        if (existingArtifact != null
                && !existingArtifact
                        .getSeries()
                        .equals(context.getVersionSeries().getIdentifier())) {

            context.pushService(new ErrorThrowingService(JsonFormSubmitException.builder()
                    .errorType(Vocabulary.u_i_ontopus_problem_already_exists)
                    .internalMessage("Unable to publish existing version as a new ontology")
                    .titleMessageCode("ontopus.core.error.ontologyExists")
                    .detailMessageArguments(
                            new Object[] {context.getVersionSeries().getIdentifier(), existingArtifact.getVersionUri()})
                    .detailMessageCode("ontopus.core.error.versionSeriesMismatch")
                    .build()));
        }
    }

    /**
     * Resolves the {@link VersionArtifact} from the given context.
     *
     * @param context the import context
     * @return Existing {@link VersionArtifact} from the database or {@code null}
     */
    @Nullable private VersionArtifact findExisting(ImportProcessContext context) {
        final String version = context.getVersionArtifact().getVersion();
        final OntologyVersionURI versionURI = context.getVersionArtifact().getVersionUri();
        Objects.requireNonNull(version, "Version of the version artifact must not be null");
        Objects.requireNonNull(versionURI, "Version URI of the version artifact must not be null");
        return versionArtifactService.findByVersionUri(versionURI).orElse(null);
    }

    /**
     * Provides informative {@link JsonForm} explaining to the user that matching {@link VersionArtifact} already exists
     * and will be overwritten.
     *
     * @param context The import process context. Contents should not be modified.
     * @param previousFormData The data submitted in the previous import process of the ontology version series.
     * @return the form or {@code null} if no artifact was found or the process is non-interactive.
     */
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
        entityMapper.copy(existingArtifact, newArtifact);
        return null;
    }
}
