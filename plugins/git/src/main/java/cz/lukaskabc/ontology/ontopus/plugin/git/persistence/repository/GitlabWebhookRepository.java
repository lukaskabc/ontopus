package cz.lukaskabc.ontology.ontopus.plugin.git.persistence.repository;

import cz.lukaskabc.ontology.ontopus.core_model.config.OntopusConfig;
import cz.lukaskabc.ontology.ontopus.plugin.git.gitlab.GitlabWebhookURI;
import cz.lukaskabc.ontology.ontopus.plugin.git.model.GitlabWebhook;
import cz.lukaskabc.ontology.ontopus.plugin.git.persistence.dao.GitlabWebhookDao;
import org.springframework.stereotype.Repository;
import org.springframework.validation.Validator;

@Repository
public class GitlabWebhookRepository extends GitWebhookRepository<GitlabWebhookURI, GitlabWebhook, GitlabWebhookDao> {
    public GitlabWebhookRepository(GitlabWebhookDao dao, Validator validator, OntopusConfig config) {
        super(dao, validator, config);
    }
}
