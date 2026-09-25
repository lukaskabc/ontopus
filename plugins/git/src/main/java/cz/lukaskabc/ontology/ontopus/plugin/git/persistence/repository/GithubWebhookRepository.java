package cz.lukaskabc.ontology.ontopus.plugin.git.persistence.repository;

import cz.lukaskabc.ontology.ontopus.core_model.config.OntopusConfig;
import cz.lukaskabc.ontology.ontopus.plugin.git.github.GithubWebhookURI;
import cz.lukaskabc.ontology.ontopus.plugin.git.model.GithubWebhook;
import cz.lukaskabc.ontology.ontopus.plugin.git.persistence.dao.GithubWebhookDao;
import org.springframework.stereotype.Repository;
import org.springframework.validation.Validator;

@Repository
public class GithubWebhookRepository extends GitWebhookRepository<GithubWebhookURI, GithubWebhook, GithubWebhookDao> {

    public GithubWebhookRepository(GithubWebhookDao dao, Validator validator, OntopusConfig config) {
        super(dao, validator, config);
    }
}
