package cz.lukaskabc.ontology.ontopus.core.service;

import cz.lukaskabc.ontology.ontopus.core_model.config.OntopusConfig;
import java.nio.file.Path;
import java.util.UUID;
import org.springframework.stereotype.Service;

/** */
@Service
public class OntologyFileService {
    private final OntopusConfig.Files fileConfig;

    public OntologyFileService(OntopusConfig config) {
        this.fileConfig = config.getFiles();
    }

    public Path createArtifactImportFolder() {
        return Path.of(fileConfig.getImportFilesDirectory().toString() + "_"
                + UUID.randomUUID().toString());
    }

    public Path relativizeArtifactImportFolder(Path artifactPath) {
        return artifactPath.relativize(fileConfig.getImportFilesDirectory());
    }
}
