package cz.lukaskabc.ontology.ontopus.plugin.git.gitlab;

import cz.lukaskabc.ontology.ontopus.plugin.git.model.GitlabWebhook;
import cz.lukaskabc.ontology.ontopus.plugin.git.persistence.repository.GitlabWebhookRepository;
import cz.lukaskabc.ontology.ontopus.plugin.git.webhook.WebhookService;
import org.springframework.stereotype.Service;

@Service
public class GitlabWebhookService extends WebhookService<GitlabWebhookURI, GitlabWebhook, GitlabWebhookRepository> {
    public GitlabWebhookService(GitlabWebhookRepository repository) {
        super(repository);
    }
}
