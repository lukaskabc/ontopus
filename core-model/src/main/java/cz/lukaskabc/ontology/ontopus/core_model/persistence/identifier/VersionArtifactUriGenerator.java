package cz.lukaskabc.ontology.ontopus.core_model.persistence.identifier;

import cz.cvut.kbss.jopa.model.EntityManager;
import cz.lukaskabc.ontology.ontopus.core_model.config.OntopusConfig;
import cz.lukaskabc.ontology.ontopus.core_model.model.id.VersionArtifactURI;
import cz.lukaskabc.ontology.ontopus.core_model.model.ontology.VersionArtifact;
import cz.lukaskabc.ontology.ontopus.core_model.model.ontology.VersionArtifact_;
import cz.lukaskabc.ontology.ontopus.core_model.util.StringUtils;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.util.Objects;

/** Creates an identifier for the {@link VersionArtifact} by sanitizing the title and the version. */
@Component
public class VersionArtifactUriGenerator extends AbstractIdentifierGenerator<VersionArtifactURI, VersionArtifact> {

    public VersionArtifactUriGenerator(EntityManager entityManager, OntopusConfig config) {
        super(entityManager, config);
    }

    @NonNull @Override
    public VersionArtifactURI generate(VersionArtifact entity) {
        return generate(entity, true);
    }

    public VersionArtifactURI generate(VersionArtifact entity, boolean checkUnique) {
        Objects.requireNonNull(entity);
        String title = sanitizeString(entity.getTitle());
        String version = StringUtils.sanitize(entity.getVersion());
        String baseId = VersionArtifact_.entityClassIRI + "/" + title + "/" + version;

        // Not trying more attempts, the ID should be unique for the given version
        URI generated = URI.create(baseId);
        if (!checkUnique || isUnique(generated)) {
            return new VersionArtifactURI(generated);
        }

        throw failedToGenerate(entity);
    }
}
