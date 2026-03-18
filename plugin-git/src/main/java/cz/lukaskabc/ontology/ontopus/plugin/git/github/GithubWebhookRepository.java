package cz.lukaskabc.ontology.ontopus.plugin.git.github;

import cz.lukaskabc.ontology.ontopus.core_model.persistence.repository.base.AbstractRepository;
import org.springframework.validation.Validator;

public class GithubWebhookRepository extends AbstractRepository<GithubWebhookURI, GithubWebhook, GithubWebhookDao> {

    public GithubWebhookRepository(GithubWebhookDao dao, Validator validator) {
        super(dao, validator, null);
    }
}
