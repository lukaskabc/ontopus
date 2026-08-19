package cz.lukaskabc.ontology.ontopus.plugin.alias.config;

import static org.junit.jupiter.api.Assertions.*;

import cz.lukaskabc.ontology.ontopus.core_model.config.OntopusConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.net.URI;
import java.util.stream.Stream;

class URIAliasPluginConfigTest {
    OntopusConfig ontopusConfig;
    URIAliasPluginConfig sut;

    @ParameterizedTest
    @ValueSource(strings = {"version-series", "version-artifact"})
    void defaultGlobalAliasesContainSlashDelimitedDcatEntityIdentifiers(String entity) {
        final long count = sut.getGlobalAliases().entrySet().stream()
                .flatMap(entry -> Stream.of(entry.getKey(), entry.getValue()))
                .map(URI::toString)
                .filter(uri -> uri.endsWith("/" + entity + "/"))
                .count();
        assertEquals(
                1, count, "There must be exactly one default mapping for identifier base of DCAT entity: " + entity);
    }

    /** Ensure that no {@code //} was created due to concatenation error. */
    @Test
    void defaultGlobalAliasesDoNotContainDoubleSlash() {
        sut.getGlobalAliases().entrySet().stream()
                .flatMap(entry -> Stream.of(entry.getKey(), entry.getValue()))
                .map(URI::toString)
                .map(uri -> {
                    assertTrue(uri.startsWith("http://"));
                    return uri.substring("http://".length());
                })
                .forEach(uri -> assertFalse(uri.contains("//"), "<" + uri + "> must not contain '//'"));
    }

    @BeforeEach
    void setUp() {
        ontopusConfig = new OntopusConfig();
        sut = new URIAliasPluginConfig(ontopusConfig);
    }
}
