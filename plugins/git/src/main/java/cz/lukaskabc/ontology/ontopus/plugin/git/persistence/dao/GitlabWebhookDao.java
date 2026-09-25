package cz.lukaskabc.ontology.ontopus.plugin.git.persistence.dao;

import cz.cvut.kbss.jopa.model.EntityManager;
import cz.lukaskabc.ontology.ontopus.plugin.git.gitlab.GitlabWebhookURI;
import cz.lukaskabc.ontology.ontopus.plugin.git.model.GitlabWebhook;
import cz.lukaskabc.ontology.ontopus.plugin.git.model.GitlabWebhook_;
import org.springframework.stereotype.Component;

@Component
public class GitlabWebhookDao extends WebhookDao<GitlabWebhookURI, GitlabWebhook> {
    public GitlabWebhookDao(EntityManager em) {
        super(GitlabWebhook.class, GitlabWebhook_.entityClassIRI, em);
    }
}
