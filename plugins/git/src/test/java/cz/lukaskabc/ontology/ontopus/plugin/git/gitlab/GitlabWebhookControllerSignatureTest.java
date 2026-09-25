package cz.lukaskabc.ontology.ontopus.plugin.git.gitlab;

import static org.junit.jupiter.api.Assertions.*;

import cz.lukaskabc.ontology.ontopus.core_model.exception.OntopusSecurityException;
import org.junit.jupiter.api.Test;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

class GitlabWebhookControllerSignatureTest {
    private static final byte[] KEY = "a signing key".getBytes(StandardCharsets.UTF_8);
    private static final String TOKEN = "whsec_" + Base64.getEncoder().encodeToString(KEY);
    private static final String MESSAGE_ID = "msg_123";
    private static final String TIMESTAMP = "1720000000";
    private static final byte[] BODY = "{\"ref\":\"refs/heads/main\"}".getBytes(StandardCharsets.UTF_8);

    @Test
    void acceptsAnyMatchingV1Signature() throws Exception {
        byte[] signature =
                GitlabWebhookController.computeSignatureBytes(TOKEN, MESSAGE_ID, TIMESTAMP, ByteBuffer.wrap(BODY));
        String header = "v1,invalid v1," + Base64.getEncoder().encodeToString(signature);

        assertDoesNotThrow(() -> GitlabWebhookController.validateEventSignature(
                TOKEN, MESSAGE_ID, TIMESTAMP, header, ByteBuffer.wrap(BODY)));
    }

    @Test
    void computesStandardWebhookSignature() throws Exception {
        byte[] expected = Base64.getDecoder().decode("fKCPQqW4hV2/Sf8P9qcAsoTDQpabSJ/OXGQx6nCsRNw=");

        assertArrayEquals(
                expected,
                GitlabWebhookController.computeSignatureBytes(TOKEN, MESSAGE_ID, TIMESTAMP, ByteBuffer.wrap(BODY)));
    }

    @Test
    void rejectsLegacySecretInsteadOfSigningToken() {
        assertThrows(
                OntopusSecurityException.class,
                () -> GitlabWebhookController.validateEventSignature(
                        "legacy-secret", MESSAGE_ID, TIMESTAMP, "v1,aW52YWxpZA==", ByteBuffer.wrap(BODY)));
    }

    @Test
    void rejectsMissingOrInvalidSignature() {
        assertThrows(
                OntopusSecurityException.class,
                () -> GitlabWebhookController.validateEventSignature(
                        TOKEN, MESSAGE_ID, TIMESTAMP, null, ByteBuffer.wrap(BODY)));
        assertThrows(
                OntopusSecurityException.class,
                () -> GitlabWebhookController.validateEventSignature(
                        TOKEN, MESSAGE_ID, TIMESTAMP, "", ByteBuffer.wrap(BODY)));
        assertThrows(
                OntopusSecurityException.class,
                () -> GitlabWebhookController.validateEventSignature(
                        TOKEN, MESSAGE_ID, TIMESTAMP, "v1,aW52YWxpZA==", ByteBuffer.wrap(BODY)));

        assertThrows(
                OntopusSecurityException.class,
                () -> GitlabWebhookController.validateEventSignature(
                        TOKEN, MESSAGE_ID, TIMESTAMP, "v1,a", ByteBuffer.wrap(BODY)));
    }
}
