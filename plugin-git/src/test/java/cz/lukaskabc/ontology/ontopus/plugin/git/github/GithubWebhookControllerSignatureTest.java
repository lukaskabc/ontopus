package cz.lukaskabc.ontology.ontopus.plugin.git.github;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

/**
 * @see <a href=
 *     "https://docs.github.com/en/webhooks/using-webhooks/validating-webhook-deliveries#testing-the-webhook-payload-validation">Github
 *     webhook payload validation</a>
 */
class GithubWebhookControllerSignatureTest {
    final String secret = "It's a Secret to Everybody";
    final String payload = "Hello, World!";
    final String expectedSignature = "sha256=757107ea0eb2509fc211221cce984b8a37570b6d7586c22c46f4379c8b043e17";
    final ByteBuffer payloadBuffer = ByteBuffer.wrap(payload.getBytes(StandardCharsets.UTF_8));

    @Test
    void computeSignatureComputesCorrectSignature() throws Exception {
        final String computedSignature = GithubWebhookController.computeSignature(secret, payloadBuffer);

        assertEquals(expectedSignature, computedSignature);
    }

    @Test
    void validateAcceptsValidSignature() {
        assertDoesNotThrow(() -> {
            GithubWebhookController.validate(expectedSignature, secret, payloadBuffer);
        });
    }
}
