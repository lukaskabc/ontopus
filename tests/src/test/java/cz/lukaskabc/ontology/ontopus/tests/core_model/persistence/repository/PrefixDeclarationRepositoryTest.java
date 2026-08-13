package cz.lukaskabc.ontology.ontopus.tests.core_model.persistence.repository;

import static org.junit.jupiter.api.Assertions.*;

import cz.lukaskabc.ontology.ontopus.core_model.model.ontology.PrefixDeclaration;
import cz.lukaskabc.ontology.ontopus.core_model.persistence.repository.PrefixDeclarationRepository;
import cz.lukaskabc.ontology.ontopus.tests.persistence.BaseDaoTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.net.URI;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class PrefixDeclarationRepositoryTest extends BaseDaoTest {

    @Autowired
    private PrefixDeclarationRepository sut;

    @Test
    void deduplicateReplacesDuplicatesWithManagedInstances() {
        final PrefixDeclaration ex = new PrefixDeclaration("ex", URI.create("http://ex/namespace"));
        final PrefixDeclaration example = new PrefixDeclaration("example", URI.create("http://example/namespace"));
        withEntities(ex, example);

        assertNotNull(ex.getIdentifier());
        assertNotNull(example.getIdentifier());

        final PrefixDeclaration newEx =
                new PrefixDeclaration(ex.getPrefix(), ex.getNamespace().toURI());
        final PrefixDeclaration unrelated = new PrefixDeclaration("unrelated", URI.create("http://ex/unrelated"));
        Set<PrefixDeclaration> newDeclarations = new HashSet<>(List.of(newEx, unrelated));

        sut.deduplicate(newDeclarations);

        assertEquals(2, newDeclarations.size(), "The amount of declarations must remain the same");

        assertTrue(newDeclarations.contains(ex), "Collection must contain deduplicated object replacement");
        assertTrue(newDeclarations.contains(unrelated), "Collection must contain unrelated object");

        assertNotNull(newEx.getIdentifier());
        assertEquals(ex.getIdentifier(), newEx.getIdentifier());

        assertEquals("unrelated", unrelated.getPrefix());
        assertNull(unrelated.getIdentifier(), "Non existing prefix declaration must not be deduplicated");
    }
}
