package cz.lukaskabc.ontology.ontopus.core.util;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.awaitility.Awaitility.await;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class ConsumableInputStreamSourceTest {

    @TempDir
    Path tempDir;

    private File testFile;

    private void createAndDiscardSource() {
        new ConsumableInputStreamSource(testFile);
    }

    private void forceGc() {
        System.gc();
    }

    @BeforeEach
    void setUp() throws IOException {
        testFile = tempDir.resolve(UUID.randomUUID() + "_test-data.txt").toFile();
        Files.writeString(testFile.toPath(), "Hello World");
    }

    @Test
    void shouldDeleteFileWhenSourceIsGarbageCollectedWithoutOpeningStream() {
        // Create source in a separate scope so it can be GC'd
        createAndDiscardSource();

        // Trigger GC and wait for Cleaner
        forceGc();

        await().atMost(Duration.ofSeconds(60))
                .untilAsserted(() -> assertThat(testFile).doesNotExist());
    }

    @Test
    void shouldNotDeleteFileWhenSourceIsCollectedButStreamIsOpen() throws IOException {
        var source = new ConsumableInputStreamSource(testFile);
        InputStream stream = source.getInputStream();

        // Discard source reference
        source = null;
        assertThat(source).isNull();

        forceGc();

        // file exists as long as the stream is open
        assertThat(testFile).exists();

        stream.close();
        assertThat(testFile).doesNotExist();
    }

    @Test
    void shouldReadAndDeleteFileOnStreamClose() throws IOException {
        var source = new ConsumableInputStreamSource(testFile);

        assertThat(testFile).exists();

        try (InputStream is = source.getInputStream()) {
            assertThat(new String(is.readAllBytes(), StandardCharsets.UTF_8)).isEqualTo("Hello World");
            assertThat(testFile).exists();
        }

        // File should be deleted immediately after close
        assertThat(testFile).doesNotExist();
    }

    @Test
    void shouldThrowWhenStreamRequestedTwice() throws IOException {
        var source = new ConsumableInputStreamSource(testFile);

        try (InputStream is = source.getInputStream()) {
            assertThat(is.available()).isGreaterThan(0);
        }

        assertThat(testFile).doesNotExist();

        // Try again
        assertThatThrownBy(source::getInputStream).isInstanceOf(FileNotFoundException.class);
    }
}
