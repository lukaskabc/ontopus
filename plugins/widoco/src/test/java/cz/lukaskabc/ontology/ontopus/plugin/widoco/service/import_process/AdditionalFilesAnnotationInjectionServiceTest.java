package cz.lukaskabc.ontology.ontopus.plugin.widoco.service.import_process;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

import cz.lukaskabc.ontology.ontopus.api.model.ImportProcessContext;
import cz.lukaskabc.ontology.ontopus.core_model.model.id.OntologyURI;
import cz.lukaskabc.ontology.ontopus.core_model.model.id.ResourceURI;
import cz.lukaskabc.ontology.ontopus.core_model.model.id.TemporaryContextURI;
import cz.lukaskabc.ontology.ontopus.core_model.model.ontology.VersionArtifact;
import cz.lukaskabc.ontology.ontopus.core_model.model.ontology.VersionSeries;
import cz.lukaskabc.ontology.ontopus.core_model.model.util.FormResult;
import cz.lukaskabc.ontology.ontopus.plugin.widoco.config.WidocoPluginConfig;
import cz.lukaskabc.ontology.ontopus.plugin.widoco.persistence.service.OntologyService;
import cz.lukaskabc.ontology.ontopus.plugin.widoco.service.AdditionalFilesPersistingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;
import java.util.UUID;

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

    @ParameterizedTest
    @CsvSource({"diagram.png", "./diagram.png"})
    void resolvePropertyResolvesFileInTheSameDirectory(String filePath) throws IOException {
        final Path relativePath = Path.of(filePath);
        Files.createDirectories(tempDir);
        Files.createFile(tempDir.resolve("diagram.png"));
        mockPropertyValues(relativePath.toString());

        assertEquals(Set.of(relativePath), sut.resolveProperty(context, PROPERTY));
    }

    @Test
    void resolvePropertyResolvesPropertiesWithinDifferentLocalDirectory() throws IOException {
        final Path ontologyFile = tempDir.resolve(Path.of("subdirectory", "a", "ontology.ttl"));
        Files.createDirectories(ontologyFile.getParent());
        Files.createFile(ontologyFile);

        final Path relativePath = Path.of("../../b/image.svg");
        Files.createDirectories(tempDir.resolve("b"));
        Files.createFile(tempDir.resolve(Path.of("b", "image.svg")));
        when(context.getOntologyFilePath()).thenReturn(ontologyFile);
        mockPropertyValues(relativePath.toString());

        assertEquals(Set.of(relativePath), sut.resolveProperty(context, PROPERTY));
    }

    @Test
    void resolvePropertyResolvesPropertiesWithValidLocalPath() throws IOException {
        final Path relativePath = Path.of("images", "diagram.png");
        Files.createDirectories(tempDir.resolve(relativePath).getParent());
        Files.createFile(tempDir.resolve(relativePath));
        mockPropertyValues(relativePath.toString());

        assertEquals(Set.of(relativePath), sut.resolveProperty(context, PROPERTY));
    }

    @Test
    void resolvesRelativeImagePathAgainstOntologyFileDirectoryWhenPersisting() throws IOException {
        final Path ontologyFile = tempDir.resolve(Path.of("subdirectory", "a", "ontology.ttl"));
        Files.createDirectories(ontologyFile.getParent());
        Files.createFile(ontologyFile);

        final Path imageFile = tempDir.resolve(Path.of("b", "image.svg"));
        Files.createDirectories(imageFile.getParent());
        Files.createFile(imageFile);

        final String referencedImagePath = "../../b/image.svg";
        final Path persistedImagePath = Path.of("b", "image.svg");
        final VersionSeries actualVersionSeries = new VersionSeries();
        actualVersionSeries.setOntologyURI(ONTOLOGY_URI);
        final ImportProcessContext actualContext = new ImportProcessContext(
                UUID.randomUUID(), actualVersionSeries, DATABASE_CONTEXT, tempDir, new VersionArtifact(), true);
        actualContext.setOntologyFilePath(ontologyFile);

        when(config.getRelativeFilePathProperties()).thenReturn(Set.of(PROPERTY));
        when(ontologyService.findValue(DATABASE_CONTEXT, ONTOLOGY_URI, new ResourceURI(PROPERTY)))
                .thenReturn(Set.of(referencedImagePath));
        final AdditionalFilesAnnotationInjectionService annotationInjectionService =
                new AdditionalFilesAnnotationInjectionService(config, ontologyService);

        annotationInjectionService.handleSubmit(FormResult.EMPTY, actualContext);

        verify(ontologyService)
                .replaceObjectStringValue(
                        DATABASE_CONTEXT,
                        ONTOLOGY_URI,
                        new ResourceURI(PROPERTY),
                        Path.of(referencedImagePath).toString(),
                        persistedImagePath.toString());

        final RecordingAdditionalFilesPersistingService persistingService =
                new RecordingAdditionalFilesPersistingService();
        final Path filesDestination = tempDir.resolve("output");
        persistingService.persistAdditionalFiles(actualContext, filesDestination);

        assertEquals(imageFile, persistingService.source);
        assertEquals(filesDestination.resolve(persistedImagePath), persistingService.destination);
    }

    @BeforeEach
    void setUp() {
        lenient().when(context.getTempFolder()).thenReturn(tempDir);
    }

    private static class RecordingAdditionalFilesPersistingService extends AdditionalFilesPersistingService {
        private Path source;
        private Path destination;

        @Override
        protected void copyFile(Path source, Path destination) {
            this.source = source;
            this.destination = destination;
        }
    }
}
