package cz.lukaskabc.ontology.ontopus.plugin.git.github;

import cz.lukaskabc.ontology.ontopus.core_model.model.id.VersionSeriesURI;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.kohsuke.github.GHEventPayload;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import tools.jackson.databind.ObjectMapper;

import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;

@RestController
@RequestMapping(GithubWebhookController.PATH)
public class GithubWebhookController {
    public static final String PATH = "/plugin/git/webhook/github";
    private static final Logger log = LogManager.getLogger(GithubWebhookController.class);

    private final ObjectMapper objectMapper;

    public GithubWebhookController(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @PostMapping
    public void handleWebhook(@RequestParam("series") VersionSeriesURI series, HttpServletRequest httpRequest)
            throws IOException {
        log.info("Received GitHub webhook for series {}", series);
        var value = objectMapper.readValue(httpRequest.getInputStream(), GHEventPayload.Create.class);
        log.info("Parsed webhook payload: {}", value);
    }
}
