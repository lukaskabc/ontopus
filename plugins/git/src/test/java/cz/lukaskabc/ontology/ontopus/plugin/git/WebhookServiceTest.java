package cz.lukaskabc.ontology.ontopus.plugin.git;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import cz.lukaskabc.ontology.ontopus.core_model.model.id.VersionSeriesURI;
import cz.lukaskabc.ontology.ontopus.plugin.git.github.GithubWebhookService;
import cz.lukaskabc.ontology.ontopus.plugin.git.github.GithubWebhookURI;
import cz.lukaskabc.ontology.ontopus.plugin.git.model.GithubWebhook;
import cz.lukaskabc.ontology.ontopus.plugin.git.persistence.repository.GithubWebhookRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class WebhookServiceTest {
    private static final VersionSeriesURI SERIES = new VersionSeriesURI("https://example.com/series");
    private static final GithubWebhookURI WEBHOOK_ID = new GithubWebhookURI("https://example.com/webhook");

    @Mock
    private GithubWebhookRepository repository;

    private GithubWebhookService service;
    private GithubWebhook webhook;

    @Test
    void deletesWebhookByVersionSeriesThroughCommonService() {
        when(repository.findByVersionSeries(SERIES)).thenReturn(Optional.of(webhook));

        service.deleteByVersionSeries(SERIES);

        verify(repository).deleteById(WEBHOOK_ID);
    }

    @Test
    void findsWebhookByVersionSeriesThroughCommonService() {
        when(repository.findByVersionSeries(SERIES)).thenReturn(Optional.of(webhook));

        assertSame(webhook, service.findByVersionSeries(SERIES).orElseThrow());
    }

    @BeforeEach
    void setUp() {
        service = new GithubWebhookService(repository);
        webhook = new GithubWebhook();
        webhook.setIdentifier(WEBHOOK_ID);
    }
}
