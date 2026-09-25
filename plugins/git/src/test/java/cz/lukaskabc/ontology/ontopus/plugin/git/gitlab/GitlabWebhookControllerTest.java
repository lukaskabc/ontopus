package cz.lukaskabc.ontology.ontopus.plugin.git.gitlab;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import cz.lukaskabc.ontology.ontopus.core_model.exception.OntopusSecurityException;
import cz.lukaskabc.ontology.ontopus.core_model.model.id.VersionSeriesURI;
import cz.lukaskabc.ontology.ontopus.plugin.git.github.WebhookHandler;
import cz.lukaskabc.ontology.ontopus.plugin.git.model.GitlabWebhook;
import cz.lukaskabc.ontology.ontopus.plugin.git.model.gitlab.GitlabEvent;
import cz.lukaskabc.ontology.ontopus.plugin.git.model.gitlab.GitlabPushEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import tools.jackson.databind.ObjectMapper;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Base64;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class GitlabWebhookControllerTest {
    private static final VersionSeriesURI SERIES = new VersionSeriesURI("https://example.com/series");
    private static final byte[] BODY = "{\"ref\":\"refs/heads/main\"}".getBytes(StandardCharsets.UTF_8);
    private static final String SIGNING_TOKEN =
            "whsec_" + Base64.getEncoder().encodeToString("signing-key".getBytes(StandardCharsets.UTF_8));

    @Mock
    private WebhookHandler webhookHandler;

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private GitlabWebhookService service;

    private GitlabWebhookController controller;

    @Test
    void acceptsWebhookWithValidSignature() throws Exception {
        String messageId = "message-id";
        String timestamp = Long.toString(Instant.now().getEpochSecond());
        byte[] signature = GitlabWebhookController.computeSignatureBytes(
                SIGNING_TOKEN, messageId, timestamp, ByteBuffer.wrap(BODY));
        GitlabPushEvent payload = new GitlabPushEvent();
        payload.setRef("refs/heads/main");

        when(objectMapper.readValue(any(byte[].class), anyInt(), anyInt(), eq(GitlabPushEvent.class)))
                .thenReturn(payload);
        when(webhookHandler.handleGLEvent(any(GitlabWebhook.class), eq(payload.getRef())))
                .thenReturn(ResponseEntity.accepted().build());

        MockHttpServletRequest request = request();
        request.addHeader("webhook-id", messageId);
        request.addHeader("webhook-timestamp", timestamp);
        request.addHeader("webhook-signature", "v1," + Base64.getEncoder().encodeToString(signature));
        request.addHeader("x-gitlab-event", GitlabEvent.PUSH.getHeaderValue());

        ResponseEntity<Void> response = controller.handleEvent(SERIES, request);

        assertEquals(HttpStatus.ACCEPTED, response.getStatusCode());
    }

    @Test
    void authenticatesBeforeRejectingMissingEventType() {
        MockHttpServletRequest request = request();

        assertThrows(OntopusSecurityException.class, () -> controller.handleEvent(SERIES, request));
        verifyNoInteractions(webhookHandler, objectMapper);
    }

    @Test
    void doesNotAcceptLegacyGitlabTokenHeader() {
        MockHttpServletRequest request = spy(request());
        request.addHeader("x-gitlab-event", "Push Hook");
        request.addHeader("x-gitlab-token", "legacy-secret");
        reset(request);

        assertThrows(OntopusSecurityException.class, () -> controller.handleEvent(SERIES, request));
        verifyNoInteractions(webhookHandler, objectMapper);
        verify(request, never()).getHeader("x-gitlab-token");
        verify(request, never()).getHeaders("x-gitlab-token");
    }

    private MockHttpServletRequest request() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setContent(BODY);
        request.setContentType("application/json");
        return request;
    }

    @BeforeEach
    void setUp() {
        GitlabWebhook webhook = new GitlabWebhook();
        webhook.setSecret(SIGNING_TOKEN);
        webhook.setEvent(GitlabEvent.PUSH);
        when(service.findByVersionSeries(SERIES)).thenReturn(Optional.of(webhook));
        controller = new GitlabWebhookController(webhookHandler, objectMapper, service);
    }
}
