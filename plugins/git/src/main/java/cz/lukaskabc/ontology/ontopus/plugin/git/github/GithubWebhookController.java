package cz.lukaskabc.ontology.ontopus.plugin.git.github;

import cz.lukaskabc.ontology.ontopus.core_model.exception.*;
import cz.lukaskabc.ontology.ontopus.core_model.model.id.VersionSeriesURI;
import cz.lukaskabc.ontology.ontopus.plugin.git.model.GithubWebhook;
import cz.lukaskabc.ontology.ontopus.plugin.git.model.github.GithubCreateEvent;
import cz.lukaskabc.ontology.ontopus.plugin.git.model.github.GithubEvent;
import cz.lukaskabc.ontology.ontopus.plugin.git.model.github.GithubPushEvent;
import cz.lukaskabc.ontology.ontopus.plugin.git.model.github.GithubRefEventBase;
import cz.lukaskabc.ontology.ontopus.plugin.git.webhook.AbstractWebhookController;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jspecify.annotations.Nullable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import tools.jackson.databind.ObjectMapper;

import jakarta.servlet.http.HttpServletRequest;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;
import java.util.Locale;
import java.util.Objects;
import java.util.function.BiFunction;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

@RestController
@RequestMapping(GithubWebhookController.PATH)
public class GithubWebhookController extends AbstractWebhookController<GithubWebhookURI, GithubWebhook> {
    public static final String PATH = "/public/plugin/git/webhook/github";
    private static final String SIGNATURE_HEADER_PREFIX = "sha256=";
    private static final Logger log = LogManager.getLogger(GithubWebhookController.class);
    private static final String ALGORITHM = "HmacSHA256";

    static byte[] computeSignatureBytes(String secret, ByteBuffer body)
            throws NoSuchAlgorithmException, InvalidKeyException {
        Objects.requireNonNull(secret, "Expected secret cannot be null");

        Mac hmac = Mac.getInstance(ALGORITHM);
        SecretKeySpec secretKeySpec = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), ALGORITHM);
        hmac.init(secretKeySpec);
        hmac.update(body);
        body.rewind();
        return hmac.doFinal();
    }

    @Nullable private static GithubEvent getEventType(HttpServletRequest httpRequest) {
        final String header = httpRequest.getHeader("X-GitHub-Event");
        if (header == null) {
            return null;
        }
        try {
            return GithubEvent.valueOf(header.toUpperCase(Locale.ENGLISH));
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    static void validate(byte[] eventSignature, String secret, ByteBuffer body) throws Exception {
        try {
            Objects.requireNonNull(eventSignature, "Event signature cannot be null");
            final byte[] expectedSignature = computeSignatureBytes(secret, body);

            boolean isSignatureValid = MessageDigest.isEqual(expectedSignature, eventSignature);

            if (!isSignatureValid) {
                throw invalidSignature("Invalid X-Hub-Signature-256");
            }
        } catch (InvalidKeyException e) {
            throw invalidSignature("Failed to calculate webhook signature", e);
        }
    }

    static void validateEventSignature(String secret, String eventSignature, ByteBuffer bodyBuffer) throws Exception {
        if (secret == null
                || secret.isEmpty()
                || eventSignature == null
                || !eventSignature.startsWith(SIGNATURE_HEADER_PREFIX)) {
            throw invalidSignature("Missing X-Hub-Signature-256 or invalid signature header");
        }
        final byte[] signature = HexFormat.of().parseHex(eventSignature.substring(SIGNATURE_HEADER_PREFIX.length()));
        validate(signature, secret, bodyBuffer);
    }

    public GithubWebhookController(
            WebhookHandler webhookHandler, ObjectMapper objectMapper, GithubWebhookService service) {
        super(webhookHandler, objectMapper, service, log, "GithubWebhook");
    }

    @Operation(
            summary = "Handles webhook payloads from GitHub",
            responses = {
                @ApiResponse(
                        responseCode = "202",
                        description = "The payload was accepted and a new import process was scheduled."),
                @ApiResponse(
                        responseCode = "204",
                        description =
                                "The payload was successfully validated, but it did not match the required criteria."),
                @ApiResponse(responseCode = "403", description = "The validation of payload signature failed.")
            })
    @PostMapping(consumes = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<Void> handleEvent(
            @RequestParam("series") VersionSeriesURI series, HttpServletRequest httpRequest) throws Exception {
        validateContentLength(httpRequest);

        final GithubEvent type = getEventType(httpRequest);
        if (type == null) {
            log.debug("Received GitHub webhook request with missing or invalid X-GitHub-Event header, ignoring");
            throw ValidationException.builder()
                    .internalMessage("No github event type specified")
                    .detailMessageArguments(OntopusException.EMPTY_ARGUMENTS)
                    .build();
        }

        ValidatedRequest<GithubWebhook> validatedRequest = prepareRequest(series, httpRequest);
        GithubWebhook webhook = validatedRequest.webhook();
        ByteBuffer bodyBuffer = validatedRequest.body();

        return switch (type) {
            case CREATE -> handleGHEvent(bodyBuffer, webhook, GithubCreateEvent.class, webhookHandler::handleGHEvent);
            case PUSH -> handleGHEvent(bodyBuffer, webhook, GithubPushEvent.class, webhookHandler::handleGHEvent);
            default -> throw ValidationException.fromValidationError("Unsupported GitHub event type: " + type);
        };
    }

    private <T extends GithubRefEventBase> ResponseEntity<Void> handleGHEvent(
            ByteBuffer bodyBuffer,
            GithubWebhook webhook,
            Class<T> payloadClass,
            BiFunction<GithubWebhook, T, ResponseEntity<Void>> handler) {
        log.debug(
                "Received GitHub webhook event {} for version series {}",
                webhook.getEvent().name(),
                webhook.getVersionSeries());

        final T payload = readPayload(bodyBuffer, payloadClass);
        return handler.apply(webhook, payload);
    }

    @Override
    protected void validateRequest(HttpServletRequest request, GithubWebhook webhook, ByteBuffer bodyBuffer)
            throws Exception {
        final String secret = webhook.getSecret();
        final String eventSignature = request.getHeader("X-Hub-Signature-256");
        validateEventSignature(secret, eventSignature, bodyBuffer);
    }
}
