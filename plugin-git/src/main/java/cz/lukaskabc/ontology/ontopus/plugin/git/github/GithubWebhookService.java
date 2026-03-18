package cz.lukaskabc.ontology.ontopus.plugin.git.github;

import cz.lukaskabc.ontology.ontopus.core_model.model.id.VersionSeriesURI;
import cz.lukaskabc.ontology.ontopus.core_model.service.base.BaseService;
import cz.lukaskabc.ontology.ontopus.plugin.git.model.GithubWebhook;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class GithubWebhookService extends BaseService<GithubWebhookURI, GithubWebhook, GithubWebhookRepository> {
    public GithubWebhookService(GithubWebhookRepository repository) {
        super(repository);
    }

    @Transactional
    public void deleteByVersionSeries(VersionSeriesURI artifactIdentifier) {
        final GithubWebhook toDelete =
                repository.findByVersionSeries(artifactIdentifier).orElseThrow();
        repository.delete(toDelete);
    }

    public Optional<GithubWebhook> findByVersionSeries(VersionSeriesURI versionSeriesURI) {
        return repository.findByVersionSeries(versionSeriesURI);
    }
}
