package cz.lukaskabc.ontology.ontopus.core_model.persistence.identifier;

import cz.cvut.kbss.jopa.model.EntityManager;
import cz.lukaskabc.ontology.ontopus.core_model.config.OntopusConfig;
import cz.lukaskabc.ontology.ontopus.core_model.model.id.VersionArtifactURI;
import cz.lukaskabc.ontology.ontopus.core_model.model.ontology.VersionArtifact;
import cz.lukaskabc.ontology.ontopus.core_model.util.DcatIdentifierProvider;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Component;

import java.util.Objects;

/** Creates an identifier for the {@link VersionArtifact} by sanitizing the title and the version. */
@Component
public class VersionArtifactUriGenerator extends AbstractIdentifierGenerator<VersionArtifactURI, VersionArtifact> {

    private final DcatIdentifierProvider dcatIdentifierProvider;

    public VersionArtifactUriGenerator(
            EntityManager entityManager, OntopusConfig config, DcatIdentifierProvider dcatIdentifierProvider) {
        super(entityManager, config);
        this.dcatIdentifierProvider = dcatIdentifierProvider;
    }

    @NonNull @Override
    public VersionArtifactURI generate(VersionArtifact entity) {
        return generate(entity, true);
    }

    public VersionArtifactURI generate(VersionArtifact entity, boolean checkUnique) {
        Objects.requireNonNull(entity);
        final String title = sanitizeString(entity.getTitle());
        final VersionArtifactURI generated =
                dcatIdentifierProvider.getVersionArtifactUri(entity.getVersionUri(), title, entity.getVersion());

        // Not trying more attempts, the ID should be unique for the given version
        if (!checkUnique || isUnique(generated.toURI())) {
            return generated;
        }

        throw failedToGenerate(entity);
    }
}
