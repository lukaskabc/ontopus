package cz.lukaskabc.ontology.ontopus.tests.core_model.persistence.repository;

import static org.junit.jupiter.api.Assertions.*;

import cz.lukaskabc.ontology.ontopus.core_model.model.ontology.PrefixDeclaration;
import cz.lukaskabc.ontology.ontopus.core_model.persistence.repository.PrefixDeclarationRepository;
import cz.lukaskabc.ontology.ontopus.tests.persistence.BaseDaoTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

public class PrefixDeclarationRepositoryTest extends BaseDaoTest {

    @Autowired
    private PrefixDeclarationRepository sut;

    @Test
    void deduplicateReplacesDuplicatesWithManagedInstances() {
        final PrefixDeclaration ex = new PrefixDeclaration("ex", URI.create("http://ex/namespace"));
        final PrefixDeclaration example = new PrefixDeclaration("example", URI.create("http://example/namespace"));
        withEntities(List.of(ex, example));

        assertNotNull(ex.getIdentifier());
        assertNotNull(example.getIdentifier());

        List<PrefixDeclaration> newDeclarations = new ArrayList<>(List.of(
                new PrefixDeclaration(ex.getPrefix(), ex.getNamespace().toURI()),
                new PrefixDeclaration("unrelated", URI.create("http://ex/unrelated"))));

        sut.deduplicate(newDeclarations);

        assertEquals(2, newDeclarations.size(), "The amount of declarations must remain the same");
        assertNotNull(
                newDeclarations.getFirst().getIdentifier(), "Deduplicated object must have an existing identifier");
        assertEquals(ex, newDeclarations.getFirst(), "Existing declaration must be deduplicated");

        assertEquals("unrelated", newDeclarations.getLast().getPrefix());
        assertNull(
                newDeclarations.getLast().getIdentifier(), "Non existing prefix declaration must not be deduplicated");
    }
}
