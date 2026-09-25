package cz.lukaskabc.ontology.ontopus.plugin.git.gitlab;

import cz.lukaskabc.ontology.ontopus.core_model.exception.*;
import cz.lukaskabc.ontology.ontopus.core_model.generated.Vocabulary;
import cz.lukaskabc.ontology.ontopus.core_model.model.id.VersionSeriesURI;
import cz.lukaskabc.ontology.ontopus.core_model.util.StringUtils;
import cz.lukaskabc.ontology.ontopus.plugin.git.github.WebhookHandler;
import cz.lukaskabc.ontology.ontopus.plugin.git.model.GitlabWebhook;
import cz.lukaskabc.ontology.ontopus.plugin.git.model.gitlab.GitlabEvent;
import cz.lukaskabc.ontology.ontopus.plugin.git.model.gitlab.GitlabPushEvent;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jspecify.annotations.Nullable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import tools.jackson.databind.ObjectMapper;

import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Duration;
import java.util.Base64;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

@RestController
@RequestMapping(GitlabWebhookController.PATH)
public class GitlabWebhookController {
    public static final String PATH = "/public/plugin/git/webhook/gitlab";
    static final String ID_HEADER = "webhook-id";
    static final String TIMESTAMP_HEADER = "webhook-timestamp";
    static final String SIGNATURE_HEADER = "webhook-signature";
    private static final String EVENT_HEADER = "x-gitlab-event";
    private static final String SIGNING_TOKEN_PREFIX = "whsec_";
    private static final String SIGNATURE_PREFIX = "v1,";
    private static final Duration TIMESTAMP_TOLERANCE = Duration.ofMinutes(5);
    private static final int REQUEST_BODY_CACHE_LIMIT = 1024 * 1024;
    private static final Logger log = LogManager.getLogger(GitlabWebhookController.class);

    static byte[] computeSignatureBytes(String signingToken, String messageId, String timestamp, ByteBuffer body)
            throws Exception {
        if (signingToken == null || !signingToken.startsWith(SIGNING_TOKEN_PREFIX)) {
            throw new IllegalArgumentException("GitLab signing token must start with whsec_");
        }
        byte[] key = Base64.getDecoder().decode(signingToken.substring(SIGNING_TOKEN_PREFIX.length()));
        Mac hmac = Mac.getInstance("HmacSHA256");
        hmac.init(new SecretKeySpec(key, "HmacSHA256"));
        hmac.update((messageId + "." + timestamp + ".").getBytes(StandardCharsets.UTF_8));
        hmac.update(body);
        body.rewind();
        return hmac.doFinal();
    }

    private static OntopusSecurityException invalidSignature(String message) {
        return invalidSignature(message, null);
    }

    private static OntopusSecurityException invalidSignature(String message, @Nullable Exception cause) {
        var builder = OntopusSecurityException.builder()
                .errorType(Vocabulary.u_i_ontopus_problem_invalid_signature)
                .internalMessage(message)
                .detailMessageArguments(OntopusException.EMPTY_ARGUMENTS)
                .titleMessageCode("ontopus.plugin.git.error.security.invalid-signature");

        if (cause != null) {
            builder.cause(cause);
        }

        return builder.build();
    }

    private static ByteBuffer readBody(HttpServletRequest request) throws IOException {
        ByteBuffer body = ByteBuffer.allocate(Math.min(request.getContentLength(), REQUEST_BODY_CACHE_LIMIT));
        try (ReadableByteChannel channel = Channels.newChannel(request.getInputStream())) {
            int read;
            do {
                read = channel.read(body);
            } while (read > 0 && body.hasRemaining());
            body.flip();
        }
        return body;
    }

    static void validateEventSignature(
            String signingToken, String messageId, String timestamp, String signatures, ByteBuffer body) {
        if (!(StringUtils.hasText(signingToken)
                && StringUtils.hasText(messageId)
                && StringUtils.hasText(timestamp)
                && StringUtils.hasText(signatures))) {
            throw invalidSignature("Missing GitLab signed webhook header");
        }
        boolean signatureMatches = false;
        try {
            String expected = SIGNATURE_PREFIX
                    + Base64.getEncoder()
                            .encodeToString(computeSignatureBytes(signingToken, messageId, timestamp, body));
            byte[] expectedBytes = expected.getBytes(StandardCharsets.UTF_8);
            for (String signature : signatures.trim().split("\\s+")) {
                if (MessageDigest.isEqual(expectedBytes, signature.getBytes(StandardCharsets.UTF_8))) {
                    signatureMatches = true;
                    break;
                }
            }
        } catch (Exception e) {
            throw invalidSignature("Malformed GitLab signed webhook token or signature", e);
        }
        if (!signatureMatches) {
            throw invalidSignature("Invalid webhook-signature");
        }
    }

    private static void validateRequest(HttpServletRequest request, GitlabWebhook webhook, ByteBuffer bodyBuffer) {
        validateEventSignature(
                webhook.getSecret(),
                request.getHeader(ID_HEADER),
                request.getHeader(TIMESTAMP_HEADER),
                request.getHeader(SIGNATURE_HEADER),
                bodyBuffer);
    }

    private final WebhookHandler webhookHandler;

    private final ObjectMapper objectMapper;

    private final GitlabWebhookService service;

    public GitlabWebhookController(
            WebhookHandler webhookHandler, ObjectMapper objectMapper, GitlabWebhookService service) {
        this.webhookHandler = webhookHandler;
        this.objectMapper = objectMapper;
        this.service = service;
    }

    @Operation(
            summary = "Handles signed webhook payloads from GitLab",
            responses = {
                @ApiResponse(
                        responseCode = "202",
                        description = "The payload was accepted and an import was scheduled."),
                @ApiResponse(
                        responseCode = "204",
                        description = "The payload was valid but did not match the required criteria."),
                @ApiResponse(responseCode = "403", description = "The validation of payload signature failed.")
            })
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> handleEvent(@RequestParam("series") VersionSeriesURI series, HttpServletRequest request)
            throws Exception {
        if (request.getContentLength() > REQUEST_BODY_CACHE_LIMIT || request.getContentLength() < 0) {
            throw ValidationExceptionBuilderStages.start()
                    .statusCode(HttpStatus.BAD_REQUEST)
                    .errorType(Vocabulary.u_i_ontopus_problem_too_large)
                    .internalMessage("Request body is too large")
                    .detailMessageArguments(OntopusException.EMPTY_ARGUMENTS)
                    .build();
        }

        GitlabWebhook webhook = service.findByVersionSeries(series)
                .orElseThrow(() -> log.throwing(NotFoundException.builder()
                        .internalMessage("GitlabWebhook is not configured for version series " + series)
                        .detailMessageArguments(OntopusException.EMPTY_ARGUMENTS)
                        .build()));

        final ByteBuffer body = readBody(request);

        validateRequest(request, webhook, body);

        GitlabEvent event = GitlabEvent.fromHeader(request.getHeader(EVENT_HEADER));
        if (event == null) {
            throw ValidationException.fromValidationError("Unsupported or missing GitLab event type");
        }
        if (event != webhook.getEvent()) return ResponseEntity.noContent().build();

        GitlabPushEvent payload = objectMapper.readValue(
                body.array(), body.arrayOffset() + body.position(), body.remaining(), GitlabPushEvent.class);

        return webhookHandler.handleGLEvent(webhook, payload.getRef());
    }
}
