package cz.lukaskabc.ontology.ontopus.plugin.alias.model;

import static org.junit.jupiter.api.Assertions.*;

import cz.lukaskabc.ontology.ontopus.core_model.exception.ValidationException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.net.URI;
import java.util.stream.Stream;

class URIAliasMappingTest {
    private static final String BASE = "http://example.com/custom/../path";
    private static final String HTTPS_BASE = "https://example.com/custom/../path";
    private static final URI BASE_URI = URI.create(BASE);

    static Stream<URI> getUrisToNormalize() {
        return Stream.of(
                        // no-op
                        "",
                        // trailing slashes
                        "/",
                        "//",
                        // fragment
                        "#fragment",
                        "#fragment/",
                        // trailing slash with fragment
                        "/#fragment",
                        "//#fragment",
                        "/#fragment/",
                        "//#fragment/",
                        "//#fragment//")
                .<String>mapMulti((suffix, consumer) -> {
                    consumer.accept(BASE + suffix);
                    consumer.accept(HTTPS_BASE + suffix);
                })
                .map(URI::create);
    }

    @Test
    void constructorThrowsWhenUrisAreEqual() {
        final URI uri = URI.create(HTTPS_BASE + "/#fragment-value");
        assertThrows(ValidationException.class, () -> new URIAliasMapping(uri, BASE_URI));
        assertThrows(ValidationException.class, () -> new URIAliasMapping(BASE_URI, uri));
    }

    @ParameterizedTest
    @MethodSource("getUrisToNormalize")
    void normalizeRemovesTrailingSlash(URI toNormalize) {
        final URI result = URIAliasMapping.normalize(toNormalize);

        assertNotNull(result);
        assertEquals(BASE, result.toString());
        assertEquals(BASE_URI, result);
    }
}
