package cz.lukaskabc.ontology.ontopus.core_model.persistence.identifier;

import cz.cvut.kbss.jopa.model.EntityManager;
import cz.lukaskabc.ontology.ontopus.core_model.config.OntopusConfig;
import cz.lukaskabc.ontology.ontopus.core_model.model.id.VersionArtifactURI;
import cz.lukaskabc.ontology.ontopus.core_model.model.ontology.VersionArtifact;
import cz.lukaskabc.ontology.ontopus.core_model.model.ontology.VersionArtifact_;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.util.Objects;

@Component
public class VersionArtifactUriGenerator extends AbstractIdentifierGenerator<VersionArtifactURI, VersionArtifact> {

    public VersionArtifactUriGenerator(EntityManager entityManager, OntopusConfig config) {
        super(entityManager, config);
    }

    @NonNull @Override
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

        throw new IllegalStateException("Unable to generate identifier for " + entity);
    }
}
