package cz.lukaskabc.ontology.ontopus.plugin.widoco.config;

import org.jspecify.annotations.NullUnmarked;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.constraints.NotNull;
import java.nio.file.Path;
import java.time.Duration;
import java.util.Map;

@Validated
@NullUnmarked
@Configuration
@ConfigurationProperties(prefix = "ontopus.plugin.widoco")
public class WidocoPluginConfig {
    /** Persistent directory for generated files */
    // @NotNull
    private Path filesDirectory;

    @NotNull private Duration executionTimeout = Duration.ofMinutes(1);

    /** Path to widoco executable jar */
    @NotNull private Path path;

    /** Widoco version to automatically download */
    private Map<String, String> downloadUrlParameters = Map.of("version", "1.4.25");

    private String downloadUrl =
            "https://github.com/dgarijo/Widoco/releases/download/v{version}/widoco-{version}-jar-with-dependencies_JDK-17.jar";

    public String getDownloadUrl() {
        return downloadUrl;
    }

    public Map<String, String> getDownloadUrlParameters() {
        return downloadUrlParameters;
    }

    public Duration getExecutionTimeout() {
        return executionTimeout;
    }

    public Path getFilesDirectory() {
        return filesDirectory;
    }

    public Path getPath() {
        return path;
    }

    public void setDownloadUrl(String downloadUrl) {
        this.downloadUrl = downloadUrl;
    }

    public void setDownloadUrlParameters(Map<String, String> downloadUrlParameters) {
        this.downloadUrlParameters = downloadUrlParameters;
    }

    public void setExecutionTimeout(Duration executionTimeout) {
        this.executionTimeout = executionTimeout;
    }

    public void setFilesDirectory(Path filesDirectory) {
        this.filesDirectory = filesDirectory;
    }

    public void setPath(Path path) {
        this.path = path;
    }
}
