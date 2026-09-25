package cz.lukaskabc.ontology.ontopus.plugin.widoco.service.import_process;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

import cz.lukaskabc.ontology.ontopus.api.model.ImportProcessContext;
import cz.lukaskabc.ontology.ontopus.core_model.model.id.OntologyURI;
import cz.lukaskabc.ontology.ontopus.core_model.model.id.ResourceURI;
import cz.lukaskabc.ontology.ontopus.core_model.model.id.TemporaryContextURI;
import cz.lukaskabc.ontology.ontopus.core_model.model.ontology.VersionSeries;
import cz.lukaskabc.ontology.ontopus.plugin.widoco.config.WidocoPluginConfig;
import cz.lukaskabc.ontology.ontopus.plugin.widoco.persistence.service.OntologyService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;

@ExtendWith(MockitoExtension.class)
class AdditionalFilesAnnotationInjectionServiceTest {
    private static final TemporaryContextURI DATABASE_CONTEXT = new TemporaryContextURI("https://example.com/context");
    private static final OntologyURI ONTOLOGY_URI = new OntologyURI("https://example.com/ontology");
    private static final URI PROPERTY = URI.create("https://schema.org/image");

    @TempDir
    Path tempDir;

    @Mock
    WidocoPluginConfig config;

    @Mock
    OntologyService ontologyService;

    @Mock
    ImportProcessContext context;

    @Mock
    VersionSeries versionSeries;

    @InjectMocks
    AdditionalFilesAnnotationInjectionService sut;

    private void mockPropertyValues(String... values) {
        when(context.getTemporaryDatabaseContext()).thenReturn(DATABASE_CONTEXT);
        when(context.getVersionSeries()).thenReturn(versionSeries);
        when(versionSeries.getOntologyURI()).thenReturn(ONTOLOGY_URI);
        when(ontologyService.findValue(DATABASE_CONTEXT, ONTOLOGY_URI, new ResourceURI(PROPERTY)))
                .thenReturn(Set.of(values));
    }

    @Test
    void resolvePropertyLeavesPropertiesWithNonExistingLocalPathUnchanged() {
        mockPropertyValues("images/missing.png");

        assertEquals(Set.of(), sut.resolveProperty(context, PROPERTY));
    }

    @Test
    void resolvePropertyLeavesPropertiesWithURIValueUnchanged() {
        mockPropertyValues("https://example.com/image.png");

        assertEquals(Set.of(), sut.resolveProperty(context, PROPERTY));
    }

    @Test
    void resolvePropertyResolvesPropertiesWithValidLocalPath() throws IOException {
        final Path relativePath = Path.of("images", "diagram.png");
        Files.createDirectories(tempDir.resolve(relativePath).getParent());
        Files.createFile(tempDir.resolve(relativePath));
        mockPropertyValues(relativePath.toString());

        assertEquals(Set.of(relativePath), sut.resolveProperty(context, PROPERTY));
    }

    @BeforeEach
    void setUp() {
        lenient().when(context.getTempFolder()).thenReturn(tempDir);
    }
}
