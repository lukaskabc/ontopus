package cz.lukaskabc.ontology.ontopus.core.persistence.identifier;

import cz.cvut.kbss.jopa.model.EntityManager;
import cz.lukaskabc.ontology.ontopus.core.config.OntopusConfig;
import cz.lukaskabc.ontology.ontopus.core.model.VersionArtifact;
import cz.lukaskabc.ontology.ontopus.core.model.VersionArtifact_;
import cz.lukaskabc.ontology.ontopus.core.model.id.VersionArtifactURI;
import java.net.URI;
import java.util.Objects;
import org.springframework.stereotype.Component;

@Component
public class ArtifactUriGenerator extends AbstractIdentifierGenerator<VersionArtifactURI, VersionArtifact> {

    public ArtifactUriGenerator(EntityManager entityManager, OntopusConfig config) {
        super(entityManager, config);
    }

    @Override
    public VersionArtifactURI generate(VersionArtifact entity) {
        Objects.requireNonNull(entity);
        String title = sanitizeString(entity::getTitle);
        String baseId = VersionArtifact_.entityClassIRI + "_" + title;

        int attempt = 0;
        while (attempt < MAX_GENERATION_ATTEMPTS) {
            URI generated = URI.create(baseId + (attempt > 0 ? attempt : ""));

            if (isUnique(generated)) {
                return new VersionArtifactURI(generated);
            }

            attempt++;
        }

        throw new IllegalStateException("Unable to generate identifier for " + entity.toString());
    }
}
