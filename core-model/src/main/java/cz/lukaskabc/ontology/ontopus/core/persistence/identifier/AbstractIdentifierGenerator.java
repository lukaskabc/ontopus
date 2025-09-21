package cz.lukaskabc.ontology.ontopus.core.persistence.identifier;

import cz.cvut.kbss.jopa.model.EntityManager;
import cz.cvut.kbss.jopa.model.MultilingualString;
import cz.lukaskabc.ontology.ontopus.core.config.OntopusConfig;
import cz.lukaskabc.ontology.ontopus.core.model.PersistenceEntity;
import cz.lukaskabc.ontology.ontopus.core.model.id.EntityIdentifier;
import java.net.URI;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Supplier;

public abstract class AbstractIdentifierGenerator<I extends EntityIdentifier, E extends PersistenceEntity<I>>
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

    protected String extractMultilingualString(Supplier<MultilingualString> supplier) {
        return extractMultilingualString(supplier.get());
    }

    protected boolean isUnique(URI identifier) {
        Objects.requireNonNull(identifier);
        return !entityManager
                .createNativeQuery(
                        """
				ASK {
				    {
				        ?uri ?po ?o .
				    } UNION {
				        ?s ?sp ?uri .
				    }
				}
				""",
                        Boolean.class)
                .setParameter("uri", identifier)
                .getSingleResult();
    }

    protected String sanitizeString(String title) {
        Objects.requireNonNull(title);
        StringBuilder sb = new StringBuilder();
        for (char c : title.toCharArray()) {
            if (Character.isAlphabetic(c)) {
                sb.append(c);
            } else if (Character.isWhitespace(c)) {
                sb.append("_");
            }
        }
        return sb.toString();
    }

    protected String sanitizeString(Supplier<MultilingualString> supplier) {
        return sanitizeString(extractMultilingualString(supplier));
    }
}
