package cz.lukaskabc.ontology.ontopus.plugin.git.persistence.repository;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import cz.lukaskabc.ontology.ontopus.core_model.config.OntopusConfig;
import cz.lukaskabc.ontology.ontopus.core_model.model.id.VersionSeriesURI;
import cz.lukaskabc.ontology.ontopus.plugin.git.model.GithubWebhook;
import cz.lukaskabc.ontology.ontopus.plugin.git.persistence.dao.GithubWebhookDao;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.validation.Validator;

import java.util.regex.Pattern;

@ExtendWith(MockitoExtension.class)
class GitWebhookRepositoryTest {
    private static final VersionSeriesURI SERIES = new VersionSeriesURI("https://example.com/series");

    @Mock
    private GithubWebhookDao dao;

    @Mock
    private Validator validator;

    @Mock
    private OntopusConfig config;

    @InjectMocks
    private GithubWebhookRepository repository;

    @Test
    void findsWebhookByVersionSeriesThroughCommonRepository() {
        GithubWebhook webhook = new GithubWebhook();
        when(dao.findByVersionSeries(SERIES)).thenReturn(webhook);

        assertSame(webhook, repository.findByVersionSeries(SERIES).orElseThrow());
    }

    @Test
    void normalizesEmptyRefPatternThroughGitWebhookRepository() {
        GithubWebhook webhook = new GithubWebhook();
        webhook.setRef(Pattern.compile(""));

        repository.persist(webhook);

        assertNull(webhook.getRef());
        verify(dao).persist(webhook);
    }
}
