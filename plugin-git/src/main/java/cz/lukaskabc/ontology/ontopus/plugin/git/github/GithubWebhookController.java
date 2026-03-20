package cz.lukaskabc.ontology.ontopus.plugin.git.github;

import cz.lukaskabc.ontology.ontopus.core_model.exception.NotFoundException;
import cz.lukaskabc.ontology.ontopus.core_model.exception.SecurityException;
import cz.lukaskabc.ontology.ontopus.core_model.model.id.VersionSeriesURI;
import cz.lukaskabc.ontology.ontopus.plugin.git.model.GithubWebhook;
import cz.lukaskabc.ontology.ontopus.plugin.git.model.github.GithubCreateEvent;
import cz.lukaskabc.ontology.ontopus.plugin.git.model.github.GithubEvent;
import cz.lukaskabc.ontology.ontopus.plugin.git.model.github.GithubPushEvent;
import cz.lukaskabc.ontology.ontopus.plugin.git.model.github.GithubRefEventBase;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jspecify.annotations.Nullable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import tools.jackson.databind.ObjectMapper;

import jakarta.servlet.http.HttpServletRequest;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;
import java.util.Objects;
import java.util.function.BiConsumer;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

@RestController
@RequestMapping(GithubWebhookController.PATH)
public class GithubWebhookController {
    public static final String PATH = "/plugin/git/webhook/github";
    private static final int MAX_GH_PAYLOAD = 1; // payloads are capped at 25 MB, allowing 1 MB at most, only small
    // events should be sent
    private static final int REQUEST_BODY_CACHE_LIMIT = MAX_GH_PAYLOAD * 1024 * 1024; // bytes
    private static final String SIGNATURE_HEADER_PREFIX = "sha256=";
    private static final Logger log = LogManager.getLogger(GithubWebhookController.class);

    static byte[] computeSignatureBytes(String secret, ByteBuffer body)
            throws NoSuchAlgorithmException, InvalidKeyException {
        Objects.requireNonNull(secret, "Expected secret cannot be null");

        Mac hmac = Mac.getInstance("HmacSHA256");
        SecretKeySpec secretKeySpec = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
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
            return GithubEvent.valueOf(header.toUpperCase());
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
                throw new SecurityException("Missing or invalid X-Hub-Signature-256");
            }
        } catch (InvalidKeyException e) {
            throw new SecurityException("Failed to calculate webhook signature", e);
        }
    }

    static void validateEventSignature(String secret, String eventSignature, ByteBuffer bodyBuffer) throws Exception {
        if (secret == null
                || secret.isEmpty()
                || eventSignature == null
                || !eventSignature.startsWith(SIGNATURE_HEADER_PREFIX)) {
            throw new SecurityException("Missing or invalid X-Hub-Signature-256");
        }
        final byte[] signature = HexFormat.of().parseHex(eventSignature.substring(SIGNATURE_HEADER_PREFIX.length()));
        validate(signature, secret, bodyBuffer);
    }

    private final WebhookHandler webhookHandler;

    private final ObjectMapper objectMapper;

    private final GithubWebhookService service;

    public GithubWebhookController(
            WebhookHandler webhookHandler, ObjectMapper objectMapper, GithubWebhookService service) {
        this.webhookHandler = webhookHandler;
        this.objectMapper = objectMapper;
        this.service = service;
    }

    @PostMapping
    public void handleEvent(@RequestParam("series") VersionSeriesURI series, HttpServletRequest httpRequest)
            throws Exception {
        if (httpRequest.getContentLength() > REQUEST_BODY_CACHE_LIMIT || httpRequest.getContentLength() < 0) {
            throw new IllegalStateException("Request body is too large");
        }

        final GithubEvent type = getEventType(httpRequest);
        if (type == null) {
            log.debug("Received GitHub webhook request with missing or invalid X-GitHub-Event header, ignoring");
            return;
        }

        final GithubWebhook webhook = service.findByVersionSeries(series)
                .orElseThrow(() -> new NotFoundException("Webhook not found for version series"));
        final ByteBuffer bodyBuffer =
                ByteBuffer.allocate(Math.min(httpRequest.getContentLength(), REQUEST_BODY_CACHE_LIMIT));

        try (final ReadableByteChannel bodyChannel = Channels.newChannel(httpRequest.getInputStream())) {
            int added;
            do {
                added = bodyChannel.read(bodyBuffer);
            } while (added > 0 && bodyBuffer.hasRemaining());
            bodyBuffer.flip();
        }

        validateRequest(httpRequest, webhook, bodyBuffer);

        switch (type) {
            case CREATE -> handleGHEvent(bodyBuffer, webhook, GithubCreateEvent.class, webhookHandler::handleGHEvent);
            case PUSH -> handleGHEvent(bodyBuffer, webhook, GithubPushEvent.class, webhookHandler::handleGHEvent);
            default -> throw new IllegalStateException("Unsupported GitHub event type: " + type);
        }
    }

    private <T extends GithubRefEventBase> void handleGHEvent(
            ByteBuffer bodyBuffer, GithubWebhook webhook, Class<T> payloadClass, BiConsumer<GithubWebhook, T> handler) {
        log.debug(
                "Received GitHub webhook event {} for version series {}",
                webhook.getEvent().name(),
                webhook.getVersionSeries());

        final T payload = objectMapper.readValue(
                bodyBuffer.array(),
                bodyBuffer.arrayOffset() + bodyBuffer.position(),
                bodyBuffer.remaining(),
                payloadClass);
        handler.accept(webhook, payload);
    }

    private void validateRequest(HttpServletRequest request, GithubWebhook webhook, ByteBuffer bodyBuffer)
            throws Exception {
        final String secret = webhook.getSecret();
        final String eventSignature = request.getHeader("X-Hub-Signature-256");
        validateEventSignature(secret, eventSignature, bodyBuffer);
    }
}
