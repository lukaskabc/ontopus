package cz.lukaskabc.ontology.ontopus.tests.plugin.alias.persistence.dao;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import cz.lukaskabc.ontology.ontopus.core_model.generated.Vocabulary;
import cz.lukaskabc.ontology.ontopus.plugin.alias.model.URIAliasMapping;
import cz.lukaskabc.ontology.ontopus.plugin.alias.persistence.dao.URIAliasMappingDao;
import cz.lukaskabc.ontology.ontopus.tests.persistence.BaseDaoTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;

import java.net.URI;

@Import(URIAliasMappingDao.class)
public class URIAliasMappingDaoTest extends BaseDaoTest {
    @Autowired
    URIAliasMappingDao sut;

    @Test
    void findAliasForFindsAliasForGivenURI() {
        final String resource = "http://example.com/resource";
        final URIAliasMapping mapping =
                new URIAliasMapping(URI.create(resource), URI.create("http://example.com/alias"));
        final URIAliasMapping unrelated = new URIAliasMapping(
                URI.create("http://example.com/unrelated/resource"), URI.create("http://example.com/unrelated/alias"));

        transactional(() -> sut.save(unrelated));
        transactional(() -> sut.save(mapping));

        final URI alias = transactional(() -> sut.findAliasFor(URI.create("http://example.com/resource")));
        assertEquals(mapping.getAlias(), alias);
    }

    @Test
    void saveSavesTheMappingTripleToTheGraph() {
        final URIAliasMapping mapping =
                new URIAliasMapping(URI.create("http://example.com/resource"), URI.create("http://example.com/alias"));

        transactional(() -> sut.save(mapping));

        assertTrue(tripleExists(
                mapping.getResource(),
                Vocabulary.u_p_ontopus_hasAlias,
                mapping.getAlias(),
                Vocabulary.u_c_ontopus_URIAliasMapping));
    }
}
