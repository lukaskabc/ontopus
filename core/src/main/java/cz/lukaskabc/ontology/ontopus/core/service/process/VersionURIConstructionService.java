package cz.lukaskabc.ontology.ontopus.core.service.process;

import cz.lukaskabc.ontology.ontopus.api.model.ImportProcessContext;
import cz.lukaskabc.ontology.ontopus.api.model.JsonForm;
import cz.lukaskabc.ontology.ontopus.api.model.ReadOnlyImportProcessContext;
import cz.lukaskabc.ontology.ontopus.api.service.import_process.ImportProcessingService;
import cz.lukaskabc.ontology.ontopus.api.service.import_process.OntologyVersioningService;
import cz.lukaskabc.ontology.ontopus.core.exception.VersionURIConstructionException;
import cz.lukaskabc.ontology.ontopus.core_model.model.id.VersionArtifactURI;
import cz.lukaskabc.ontology.ontopus.core_model.model.id.VersionSeriesURI;
import cz.lukaskabc.ontology.ontopus.core_model.model.util.FormResult;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.util.UriComponentsBuilder;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.node.ObjectNode;

import java.net.URI;

/** {@link OntologyVersioningService} that constructs the version URI by concatenating ontology URI and version. */
@NullMarked
@Service
public class VersionURIConstructionService implements ImportProcessingService<Void> {
    private static final Logger log = LogManager.getLogger(VersionURIConstructionService.class);
    private final ObjectMapper objectMapper;

    public VersionURIConstructionService(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public @Nullable JsonForm getJsonForm(ReadOnlyImportProcessContext context) {
        // TODO pass context to get json form
        final String version = "2026-02-25";
        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(
                "http://onto.fel.cvut.cz/ontologies/slovník/agendový/popis-dat/slovník");
        builder.pathSegment("{version}");

        ObjectNode scheme = objectMapper.createObjectNode();
        scheme.put("type", "object");
        ObjectNode properties = scheme.putObject("properties");
        properties.putObject("uri").put("type", "string");
        properties.putObject("version").put("type", "string");

        ObjectNode formData = objectMapper.createObjectNode();
        formData.put("version", version);
        formData.put("uri", builder.build().toUriString());

        ObjectNode uiSchema = objectMapper.createObjectNode();
        uiSchema.put("ui:field", "versionUriField");

        return new JsonForm(scheme, uiSchema, formData);
    }

    @Override
    public String getServiceName() {
        return this.getClass().getSimpleName();
    }

    @Override
    public Void handleSubmit(FormResult formResult, ImportProcessContext context) {
        if (context.getVersionArtifact().getIdentifier() != null) {
            log.debug(
                    "Version artifact already has an identifier: {}, skipping version URI construction",
                    context.getVersionArtifact().getIdentifier());
            // no operation if there is already an identifier
            return null;
        }

        VersionSeriesURI ontologyIdentifier = seriesURI(context);

        String version = context.getVersionArtifact().getVersion();
        if (version == null) {
            log.warn(
                    "Failed to construct version URI, the ontology version is missing for version artifact: {}",
                    context.getVersionArtifact());
            return null;
        }

        URI ontologyUri = ontologyIdentifier.toURI();
        if (!ontologyUri.getPath().endsWith("/") && !StringUtils.hasText(ontologyUri.getFragment())) {
            ontologyUri = URI.create(ontologyUri + "/");
        }

        URI versionURI = ontologyUri.resolve(version);
        VersionArtifactURI versionArtifactURI = new VersionArtifactURI(versionURI);
        context.getVersionArtifact().setIdentifier(versionArtifactURI);
        return null;
    }

    /**
     * Extracts the {@link cz.lukaskabc.ontology.ontopus.core_model.model.ontology.VersionSeries} identifier from the
     * context
     *
     * @param context the import process context containing the version series information
     * @return the version series identifier as a {@link VersionSeriesURI}
     * @throws VersionURIConstructionException if the version series identifier is missing in the context
     */
    private VersionSeriesURI seriesURI(ImportProcessContext context) {
        VersionSeriesURI ontologyIdentifier = context.getVersionSeries().getIdentifier();
        if (ontologyIdentifier == null) {
            throw new VersionURIConstructionException("Failed to construct version URI, "
                    + "the ontology identifier is missing for the version series: " + context.getVersionSeries());
        }
        return ontologyIdentifier;
    }

    private String version(ImportProcessContext context) {
        String version = context.getVersionArtifact().getVersion();
        if (version == null) {
            throw new VersionURIConstructionException("Failed to construct version URI, "
                    + "the ontology version is missing for version artifact: " + context.getVersionArtifact());
        }
        return version;
    }
}
