package cz.lukaskabc.ontology.ontopus.plugin.versioning.uri;

import cz.lukaskabc.ontology.ontopus.api.model.FormResult;
import cz.lukaskabc.ontology.ontopus.api.model.ImportProcessContext;
import cz.lukaskabc.ontology.ontopus.api.model.JsonForm;
import cz.lukaskabc.ontology.ontopus.api.service.ImportProcessingService;
import cz.lukaskabc.ontology.ontopus.api.service.OntologyVersioningService;
import cz.lukaskabc.ontology.ontopus.core_model.model.id.VersionArtifactURI;
import java.net.URI;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jspecify.annotations.Nullable;
import org.springframework.stereotype.Service;

/** {@link OntologyVersioningService} that constructs the version URI by concatenating ontology URI and version. */
@Service
public class VersionURIConstructionService implements ImportProcessingService<Void> {
    private static final Logger log = LogManager.getLogger(VersionURIConstructionService.class);

    @Override
    public @Nullable JsonForm getJsonForm() {
        return null;
    }

    @Override
    public String getServiceName() {
        return "";
    }

    @Override
    public Void handleSubmit(FormResult formResult, ImportProcessContext context) {
        if (context.getVersionArtifact().getIdentifier() != null) {
            // no operation if there is already an identifier
            return null;
        }

        URI ontologyIdentifier = context.getVersionSeries().getOntologyIdentifier();
        if (ontologyIdentifier == null) {
            log.warn(
                    "Failed to construct version URI, the ontology identifier is missing for the version series: {}",
                    context.getVersionSeries());
            return null;
        }

        String version = context.getVersionArtifact().getVersion();
        if (version == null) {
            log.warn(
                    "Failed to construct version URI, the ontology version is missing for version artifact: {}",
                    context.getVersionArtifact());
            return null;
        }

        URI versionURI = ontologyIdentifier.resolve(version);
        VersionArtifactURI versionArtifactURI = new VersionArtifactURI(versionURI);
        context.getVersionArtifact().setIdentifier(versionArtifactURI);
        context.getVersionArtifact().setUri(versionURI);
        return null;
    }
}
