package cz.lukaskabc.ontology.ontopus.tests.plugin.alias.filter;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import cz.lukaskabc.ontology.ontopus.plugin.alias.URIAliasPlugin;
import cz.lukaskabc.ontology.ontopus.plugin.alias.persistence.service.AliasService;
import cz.lukaskabc.ontology.ontopus.tests.integration.BaseRestIntegrationTest;
import cz.lukaskabc.ontology.ontopus.tests.util.LoadPlugins;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

@LoadPlugins(URIAliasPlugin.class)
public class URIAliasFilterIntegrationTest extends BaseRestIntegrationTest {
    @Autowired
    private AliasService aliasService;

    private String dcatCatalog;

    @Test
    void catalogPathOnUnknownDomainReturnsNotFound() throws Exception {
        mockMvc.perform(get("http://unknown.domain.org/dcat/catalog")).andExpect(status().isNotFound());
    }

    @Test
    void catalogPathReturnsOk() throws Exception {
        mockMvc.perform(get(dcatCatalog).accept(MediaType.APPLICATION_XML)).andExpect(status().isOk());
    }

    @Test
    void dcatPathOnUnknownDomainReturnsNotFound() throws Exception {
        mockMvc.perform(get("http://unknown.domain.org/dcat")).andExpect(status().isNotFound());
    }

    @Test
    void dcatPathRedirectsToCatalog() throws Exception {
        getAndExpectRedirectTo(config.getSystemUri().resolve("/dcat").toString(), dcatCatalog);
    }

    private void getAndExpectRedirectTo(String url, String expectedDestination) throws Exception {
        mockMvc.perform(get(url))
                .andExpect(status().isFound())
                .andExpect(header().string(HttpHeaders.LOCATION, expectedDestination));
    }

    @BeforeEach
    void setUp() {
        this.dcatCatalog = config.getSystemUri().resolve("/dcat/catalog").toString();
    }

    @Test
    void systemUriRedirectsToAdminPath() throws Exception {
        getAndExpectRedirectTo(
                config.getSystemUri().toString(),
                config.getSystemUri().resolve("admin").toString());
    }

    @Test
    void unknownDcatPathReturnsNotFound() throws Exception {
        mockMvc.perform(get(config.getSystemUri().resolve("/dcat/unknown"))).andExpect(status().isNotFound());
    }

    @Test
    void unknownDomainReturnsNotFound() throws Exception {
        mockMvc.perform(get("http://unknown.domain.org")).andExpect(status().isNotFound());
    }

    @Test
    void unknownPublicSystemUriPathReturnsNotFound() throws Exception {
        mockMvc.perform(get(config.getSystemUri().resolve("/public/unknown"))).andExpect(status().isNotFound());
    }

    @Test
    void unknownSystemUriPathReturnsForbidden() throws Exception {
        mockMvc.perform(get(config.getSystemUri().resolve("/unknown"))).andExpect(status().isForbidden());
    }

    @Test
    void versionArtifactPathRedirectsToCatalog() throws Exception {
        getAndExpectRedirectTo(
                config.getSystemUri().resolve("/dcat/version-artifact").toString(), dcatCatalog);
    }

    @Test
    void versionSeriesPathRedirectsToCatalog() throws Exception {
        getAndExpectRedirectTo(
                config.getSystemUri().resolve("/dcat/version-series").toString(), dcatCatalog);
    }
}
