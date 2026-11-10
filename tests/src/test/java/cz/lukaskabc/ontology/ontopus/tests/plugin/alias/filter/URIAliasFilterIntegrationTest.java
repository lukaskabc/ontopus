package cz.lukaskabc.ontology.ontopus.tests.plugin.alias.filter;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import cz.lukaskabc.ontology.ontopus.plugin.alias.URIAliasPlugin;
import cz.lukaskabc.ontology.ontopus.plugin.alias.persistence.service.AliasService;
import cz.lukaskabc.ontology.ontopus.tests.integration.BaseRestIntegrationTest;
import cz.lukaskabc.ontology.ontopus.tests.util.LoadPlugins;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@LoadPlugins(URIAliasPlugin.class)
public class URIAliasFilterIntegrationTest extends BaseRestIntegrationTest {
    @Autowired
    private AliasService aliasService;

    @Test
    void test() {
        assertNotNull(aliasService);
        // assertNotNull(mockMvc);
    }
}
