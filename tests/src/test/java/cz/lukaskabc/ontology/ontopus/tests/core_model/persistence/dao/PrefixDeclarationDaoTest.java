package cz.lukaskabc.ontology.ontopus.tests.core_model.persistence.dao;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import cz.lukaskabc.ontology.ontopus.core_model.model.ontology.PrefixDeclaration;
import cz.lukaskabc.ontology.ontopus.core_model.persistence.dao.PrefixDeclarationDao;
import cz.lukaskabc.ontology.ontopus.tests.persistence.BaseDaoTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.net.URI;
import java.util.List;

public class PrefixDeclarationDaoTest extends BaseDaoTest {
    @Autowired
    PrefixDeclarationDao sut;

    @Test
    void findByPrefixAndNamespaceFindMatchingPrefixDeclaration() {
        final PrefixDeclaration existing = new PrefixDeclaration("ex", URI.create("http://ex/namespace"));
        final PrefixDeclaration unrelatedNamespace = new PrefixDeclaration("ex", URI.create("http://ex/unrelated"));
        final PrefixDeclaration unrelatedPrefix = new PrefixDeclaration("unrelated", URI.create("http://ex/namespace"));
        final PrefixDeclaration unrelated = new PrefixDeclaration("unrelated", URI.create("http://ex/unrelated"));

        withEntities(List.of(existing, unrelatedNamespace, unrelatedPrefix, unrelated));

        final PrefixDeclaration result = sut.findByPrefixAndNamespace(existing.getPrefix(), existing.getName());
        assertNotNull(result);
        assertNotNull(result.getIdentifier());
        assertEquals(existing, result);
    }
}
