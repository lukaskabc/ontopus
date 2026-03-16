package cz.lukaskabc.ontology.ontopus.core_model.persistence.identifier;

import cz.cvut.kbss.jopa.model.EntityManager;
import cz.cvut.kbss.jopa.model.MultilingualString;
import cz.lukaskabc.ontology.ontopus.core_model.config.OntopusConfig;
import cz.lukaskabc.ontology.ontopus.core_model.model.PersistenceEntity;
import cz.lukaskabc.ontology.ontopus.core_model.model.id.TypedIdentifier;
import cz.lukaskabc.ontology.ontopus.core_model.util.StringUtils;

import java.net.URI;
import java.util.Objects;
import java.util.Optional;

public abstract class AbstractIdentifierGenerator<I extends TypedIdentifier, E extends PersistenceEntity<I>>
        implements IdentifierGenerator<I, E> {
    protected static final int MAX_GENERATION_ATTEMPTS = 15;

    private static IllegalArgumentException invalidMultilingualString() {
        return new IllegalArgumentException("Multilingual string cannot be blank");
    }

    protected final EntityManager entityManager;
    protected final String databaseLanguage;

    public AbstractIdentifierGenerator(EntityManager entityManager, OntopusConfig config) {
        this.entityManager = entityManager;
        this.databaseLanguage = config.getDatabase().getLanguage();
    }

    protected String extractMultilingualString(MultilingualString multilingualString) {
        Objects.requireNonNull(multilingualString);
        if (multilingualString.isEmpty()) {
            throw invalidMultilingualString();
        }

        final String value = Optional.ofNullable(multilingualString.get(null))
                .or(() -> Optional.ofNullable(multilingualString.get(databaseLanguage)))
                .or(() -> Optional.ofNullable(multilingualString.get()))
                .orElseThrow(AbstractIdentifierGenerator::invalidMultilingualString);

        if (value == null || value.isEmpty()) {
            throw invalidMultilingualString();
        }
        return value;
    }

    protected boolean isUnique(URI identifier) {
        Objects.requireNonNull(identifier);
        return !entityManager
                .createNativeQuery("""
				ASK {
				    {
				        ?uri ?po ?o .
				    } UNION {
				        ?s ?sp ?uri .
				    }
				}
				""", Boolean.class)
                .setParameter("uri", identifier)
                .getSingleResult();
    }

    protected String sanitizeString(MultilingualString multilingualString) {
        return StringUtils.sanitize(extractMultilingualString(multilingualString));
    }

    public void setIdentifierIfMissing(E entity) {
        if (entity.getIdentifier() == null) {
            final I identifier = generate(entity);
            if (identifier == null) {
                throw new IllegalStateException("Failed to generate an identifier for entity " + entity);
            }
            entity.setIdentifier(identifier);
        }
    }
}
