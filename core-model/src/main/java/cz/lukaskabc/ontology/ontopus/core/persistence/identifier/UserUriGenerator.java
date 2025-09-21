package cz.lukaskabc.ontology.ontopus.core.persistence.identifier;

import cz.cvut.kbss.jopa.model.EntityManager;
import cz.lukaskabc.ontology.ontopus.core.config.OntopusConfig;
import cz.lukaskabc.ontology.ontopus.core.model.User;
import cz.lukaskabc.ontology.ontopus.core.model.User_;
import cz.lukaskabc.ontology.ontopus.core.model.id.UserURI;
import java.net.URI;
import java.util.Objects;
import org.springframework.stereotype.Component;

@Component
public class UserUriGenerator extends AbstractIdentifierGenerator<UserURI, User> {

    public UserUriGenerator(EntityManager entityManager, OntopusConfig config) {
        super(entityManager, config);
    }

    private void ensureHasUsername(User user) {
        Objects.requireNonNull(user);
        Objects.requireNonNull(user.getUsername());
        if (user.getUsername().isBlank()) {
            throw new IllegalArgumentException("Username cannot be blank");
        }
    }

    @Override
    public UserURI generate(User entity) {
        ensureHasUsername(entity);
        int attempt = 0;
        final String base = User_.entityClassIRI + "_" + sanitizeString(entity.getUsername());
        while (attempt < MAX_GENERATION_ATTEMPTS) {
            URI generated = URI.create(base + (attempt > 0 ? attempt : ""));

            if (isUnique(generated)) {
                return new UserURI(generated);
            }

            attempt++;
        }
        throw new IllegalStateException("Unable to generate identifier for " + entity);
    }
}
