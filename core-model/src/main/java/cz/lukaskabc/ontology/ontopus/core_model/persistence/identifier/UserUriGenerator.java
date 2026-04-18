package cz.lukaskabc.ontology.ontopus.core_model.persistence.identifier;

import cz.cvut.kbss.jopa.model.EntityManager;
import cz.lukaskabc.ontology.ontopus.core_model.config.OntopusConfig;
import cz.lukaskabc.ontology.ontopus.core_model.exception.ValidationException;
import cz.lukaskabc.ontology.ontopus.core_model.model.User;
import cz.lukaskabc.ontology.ontopus.core_model.model.User_;
import cz.lukaskabc.ontology.ontopus.core_model.model.id.UserURI;
import cz.lukaskabc.ontology.ontopus.core_model.util.StringUtils;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.util.Objects;

@Component
public class UserUriGenerator extends AbstractIdentifierGenerator<UserURI, User> {

    public UserUriGenerator(EntityManager entityManager, OntopusConfig config) {
        super(entityManager, config);
    }

    private void ensureHasUsername(User user) {
        Objects.requireNonNull(user);
        Objects.requireNonNull(user.getUsername());
        if (user.getUsername().isBlank()) {
            throw ValidationException.fromValidationError("Username cannot be blank");
        }
    }

    @Override
    public UserURI generate(User entity) {
        ensureHasUsername(entity);
        int attempt = 0;
        final String base = User_.entityClassIRI + "_" + StringUtils.sanitize(entity.getUsername());
        while (attempt < MAX_GENERATION_ATTEMPTS) {
            URI generated = URI.create(base + (attempt > 0 ? attempt : ""));

            if (isUnique(generated)) {
                return new UserURI(generated);
            }

            attempt++;
        }
        throw failedToGenerate(entity);
    }
}
