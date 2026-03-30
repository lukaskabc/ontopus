package cz.lukaskabc.ontology.ontopus.plugin.widoco.config;

import org.jspecify.annotations.NullUnmarked;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.constraints.NotNull;
import java.io.File;
import java.time.Duration;

@Validated
@NullUnmarked
@Configuration
@ConfigurationProperties(prefix = "ontopus.plugin.widoco")
public class WidocoPluginConfig {
    /** Persistent directory for generated files */
    // @NotNull
    private File filesDirectory;

    @NotNull private Duration executionTimeout = Duration.ofMinutes(1);

    /** Path to widoco executable jar */
    @NotNull private File path;

    public Duration getExecutionTimeout() {
        return executionTimeout;
    }

    public File getFilesDirectory() {
        return filesDirectory;
    }

    public File getPath() {
        return path;
    }

    public void setExecutionTimeout(Duration executionTimeout) {
        this.executionTimeout = executionTimeout;
    }

    public void setFilesDirectory(File filesDirectory) {
        this.filesDirectory = filesDirectory;
    }

    public void setPath(File path) {
        this.path = path;
    }
}
