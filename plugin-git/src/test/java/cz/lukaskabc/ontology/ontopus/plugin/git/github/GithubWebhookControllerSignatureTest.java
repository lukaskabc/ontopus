package cz.lukaskabc.ontology.ontopus.plugin.git.github;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.HexFormat;

/**
 * @see <a href=
 *     "https://docs.github.com/en/webhooks/using-webhooks/validating-webhook-deliveries#testing-the-webhook-payload-validation">Github
 *     webhook payload validation</a>
 */
class GithubWebhookControllerSignatureTest {
    final String secret = "It's a Secret to Everybody";
    final String payload = "Hello, World!";
    final String expectedSignature = "sha256=757107ea0eb2509fc211221cce984b8a37570b6d7586c22c46f4379c8b043e17";
    final byte[] expectedSignatureBytes = HexFormat.of().parseHex(expectedSignature.substring("sha256=".length()));
    final byte[] payloadBytes = payload.getBytes(StandardCharsets.UTF_8);
    final ByteBuffer payloadBuffer = ByteBuffer.wrap(payloadBytes);

    @Test
    void computeSignatureComputesCorrectSignature() throws Exception {
        final byte[] computedSignature = GithubWebhookController.computeSignatureBytes(secret, payloadBuffer);
        assertTrue(MessageDigest.isEqual(expectedSignatureBytes, computedSignature));
    }

    @Test
    void validateEventSignatureAcceptsValidSignature() {
        assertDoesNotThrow(
                () -> GithubWebhookController.validateEventSignature(secret, expectedSignature, payloadBuffer));
    }
}
