package cz.lukaskabc.ontology.ontopus.plugin.git.github;

import cz.lukaskabc.ontology.ontopus.core_model.service.base.BaseService;
import org.springframework.stereotype.Service;

@Service
public class GithubWebhookService extends BaseService<GithubWebhookURI, GithubWebhook, GithubWebhookRepository> {
    public GithubWebhookService(GithubWebhookRepository repository) {
        super(repository);
    }
}
