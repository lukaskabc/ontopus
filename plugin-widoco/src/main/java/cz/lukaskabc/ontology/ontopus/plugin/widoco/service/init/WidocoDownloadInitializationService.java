package cz.lukaskabc.ontology.ontopus.plugin.widoco.service.init;

import cz.lukaskabc.ontology.ontopus.api.service.core.InitializationService;
import cz.lukaskabc.ontology.ontopus.core_model.exception.InitializationException;
import cz.lukaskabc.ontology.ontopus.core_model.util.StringUtils;
import cz.lukaskabc.ontology.ontopus.plugin.widoco.config.WidocoPluginConfig;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.FileOutputStream;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Path;

@Order(600)
@Component
public class WidocoDownloadInitializationService implements InitializationService {
    private static final Logger log = LogManager.getLogger(WidocoDownloadInitializationService.class);
    private final WidocoPluginConfig config;

    public WidocoDownloadInitializationService(WidocoPluginConfig config) {
        this.config = config;
    }

    private URL buildDownloadURL() {
        try {
            return UriComponentsBuilder.fromUriString(config.getDownloadUrl())
                    .build(config.getDownloadUrlParameters())
                    .toURL();
        } catch (Exception e) {
            throw new InitializationException(
                    "Failed to build Widoco download URL from template: " + config.getDownloadUrl(), e);
        }
    }

    private void downloadWidoco(Path destination, URL url) {
        log.info("Downloading Widoco from {} to {}", url, destination);

        try (ReadableByteChannel input = Channels.newChannel(url.openStream());
                FileOutputStream output = new FileOutputStream(destination.toFile())) {
            FileChannel fileChannel = output.getChannel();
            fileChannel.transferFrom(input, 0, Long.MAX_VALUE);
        } catch (Exception e) {
            throw new InitializationException("Failed to download Widoco from " + url, e);
        }
    }

    @Override
    public void initialize() {
        if (!StringUtils.hasText(config.getDownloadUrl())) {
            throw new InitializationException(
                    "Widoco download URL is not specified in configuration, unable to download");
        }

        final URL url = buildDownloadURL();
        Path path = config.getPath();
        if (path.toFile().mkdirs()) {
            log.debug("Created parent directories for Widoco executable at {}", path.toAbsolutePath());
        }
        if (path.toFile().isDirectory()) {
            path = path.resolve(resolveFileName(url));
        }
        config.setPath(path);

        if (path.toFile().isFile()) {
            log.debug("Widoco executable already exists at {}, skipping download", path.toAbsolutePath());
            return;
        }

        // downloading synchronously
        downloadWidoco(path, url);

        log.info("Widoco successfully downloaded");
    }

    private String resolveFileName(URL url) {
        final String strUrl = url.toString();
        int jarIndex = strUrl.lastIndexOf(".jar");
        int slash = -1;
        for (int i = jarIndex; i > 0; i--) {
            if (strUrl.charAt(i) == '/') {
                slash = i;
                break;
            }
        }

        if (jarIndex > slash && slash > -1) {
            return strUrl.substring(slash + 1, jarIndex + 4);
        }
        return "widoco.jar";
    }
}
