package cz.lukaskabc.ontology.ontopus.plugin.git.github;

import cz.lukaskabc.ontology.ontopus.plugin.git.model.GithubWebhook;
import cz.lukaskabc.ontology.ontopus.plugin.git.persistence.repository.GithubWebhookRepository;
import cz.lukaskabc.ontology.ontopus.plugin.git.webhook.WebhookService;
import org.springframework.stereotype.Service;

@Service
public class GithubWebhookService extends WebhookService<GithubWebhookURI, GithubWebhook, GithubWebhookRepository> {
    public GithubWebhookService(GithubWebhookRepository repository) {
        super(repository);
    }
}
