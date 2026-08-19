package cz.lukaskabc.ontology.ontopus.plugin.alias.config;

import cz.lukaskabc.ontology.ontopus.core_model.config.OntopusConfig;
import cz.lukaskabc.ontology.ontopus.core_model.util.DcatIdentifierProvider;
import org.jspecify.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

@Validated
@Configuration
@ConfigurationProperties("ontopus.plugin.alias")
public class URIAliasPluginConfig {

    private static Map<URI, URI> createDefaultGlobalAliases(OntopusConfig config) {
        final URI dcatUri = config.getDcatCatalog().getBaseUri();
        final URI catalog = URI.create(dcatUri + DcatIdentifierProvider.CATALOG_PATH);
        Map<URI, URI> aliases = new HashMap<>();
        aliases.put(config.getSystemUri(), catalog);
        aliases.put(dcatUri, catalog);
        aliases.put(URI.create(dcatUri + DcatIdentifierProvider.VERSION_SERIES_PATH), catalog);
        aliases.put(URI.create(dcatUri + DcatIdentifierProvider.VERSION_ARTIFACT_PATH), catalog);

        return aliases;
    }

    /**
     * Global URI aliases that should be always registered.
     *
     * <p>By default, maps the System URI, DCAT base URI, and the DCAT base URI with '/version-series' and
     * '/version-artifact' suffixes to the DCAT catalog URI (DCAT base URI + '/catalog').
     *
     * @configurationdoc.default Redirects root system URI and root of DCAT entities identifiers to DCAT catalog.
     */
    @Nullable private Map<URI, URI> globalAliases;

    @Autowired
    public URIAliasPluginConfig(OntopusConfig config) {
        this.globalAliases = createDefaultGlobalAliases(config);
    }

    public Map<URI, URI> getGlobalAliases() {
        if (globalAliases == null) {
            return Map.of();
        }
        return globalAliases;
    }

    public void setGlobalAliases(@Nullable Map<URI, URI> globalAliases) {
        this.globalAliases = globalAliases;
    }
}
