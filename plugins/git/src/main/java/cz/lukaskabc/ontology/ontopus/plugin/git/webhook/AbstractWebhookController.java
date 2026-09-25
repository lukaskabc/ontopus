package cz.lukaskabc.ontology.ontopus.plugin.git.webhook;

import cz.lukaskabc.ontology.ontopus.core_model.exception.NotFoundException;
import cz.lukaskabc.ontology.ontopus.core_model.exception.OntopusException;
import cz.lukaskabc.ontology.ontopus.core_model.exception.OntopusSecurityException;
import cz.lukaskabc.ontology.ontopus.core_model.exception.ValidationExceptionBuilderStages;
import cz.lukaskabc.ontology.ontopus.core_model.generated.Vocabulary;
import cz.lukaskabc.ontology.ontopus.core_model.model.id.TypedIdentifier;
import cz.lukaskabc.ontology.ontopus.core_model.model.id.VersionSeriesURI;
import cz.lukaskabc.ontology.ontopus.plugin.git.github.WebhookHandler;
import cz.lukaskabc.ontology.ontopus.plugin.git.model.Webhook;
import org.apache.logging.log4j.Logger;
import org.jspecify.annotations.Nullable;
import org.springframework.http.HttpStatus;
import tools.jackson.databind.ObjectMapper;

import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;

public abstract class AbstractWebhookController<I extends TypedIdentifier, W extends Webhook<I>> {
    private static final int REQUEST_BODY_CACHE_LIMIT = 1024 * 1024;

    protected static OntopusSecurityException invalidSignature(String internalMessage) {
        return invalidSignature(internalMessage, null);
    }

    protected static OntopusSecurityException invalidSignature(String internalMessage, @Nullable Exception cause) {
        var builder = OntopusSecurityException.builder()
                .errorType(Vocabulary.u_i_ontopus_problem_invalid_signature)
                .internalMessage(internalMessage)
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

    protected final WebhookHandler webhookHandler;

    private final ObjectMapper objectMapper;
    private final WebhookService<I, W, ?> service;
    private final Logger log;
    private final String webhookType;

    protected AbstractWebhookController(
            WebhookHandler webhookHandler,
            ObjectMapper objectMapper,
            WebhookService<I, W, ?> service,
            Logger log,
            String webhookType) {
        this.webhookHandler = webhookHandler;
        this.objectMapper = objectMapper;
        this.service = service;
        this.log = log;
        this.webhookType = webhookType;
    }

    protected final ValidatedRequest<W> prepareRequest(VersionSeriesURI series, HttpServletRequest request)
            throws Exception {
        W webhook = service.findByVersionSeries(series)
                .orElseThrow(() -> log.throwing(NotFoundException.builder()
                        .internalMessage(webhookType + " is not configured for version series " + series)
                        .detailMessageArguments(OntopusException.EMPTY_ARGUMENTS)
                        .build()));
        ByteBuffer body = readBody(request);
        validateRequest(request, webhook, body);
        return new ValidatedRequest<>(webhook, body);
    }

    protected final <T> T readPayload(ByteBuffer body, Class<T> payloadClass) {
        return objectMapper.readValue(
                body.array(), body.arrayOffset() + body.position(), body.remaining(), payloadClass);
    }

    protected final void validateContentLength(HttpServletRequest request) {
        if (request.getContentLength() > REQUEST_BODY_CACHE_LIMIT) {
            throw ValidationExceptionBuilderStages.start()
                    .statusCode(HttpStatus.CONTENT_TOO_LARGE)
                    .errorType(Vocabulary.u_i_ontopus_problem_too_large)
                    .internalMessage("Request body is too large")
                    .detailMessageArguments(OntopusException.EMPTY_ARGUMENTS)
                    .build();
        }
        if (request.getContentLength() < 0) {
            throw ValidationExceptionBuilderStages.start()
                    .statusCode(HttpStatus.LENGTH_REQUIRED)
                    .errorType(Vocabulary.u_i_ontopus_problem_length_required)
                    .internalMessage("Content length is required")
                    .detailMessageArguments(OntopusException.EMPTY_ARGUMENTS)
                    .build();
        }
    }

    protected abstract void validateRequest(HttpServletRequest request, W webhook, ByteBuffer body) throws Exception;

    protected record ValidatedRequest<T>(T webhook, ByteBuffer body) {}
}
