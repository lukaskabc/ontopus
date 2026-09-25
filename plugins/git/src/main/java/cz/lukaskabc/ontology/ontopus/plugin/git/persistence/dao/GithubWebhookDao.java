package cz.lukaskabc.ontology.ontopus.plugin.git.persistence.dao;

import cz.cvut.kbss.jopa.model.EntityManager;
import cz.lukaskabc.ontology.ontopus.plugin.git.github.GithubWebhookURI;
import cz.lukaskabc.ontology.ontopus.plugin.git.model.GithubWebhook;
import cz.lukaskabc.ontology.ontopus.plugin.git.model.GithubWebhook_;
import org.springframework.stereotype.Component;

@Component
public class GithubWebhookDao extends WebhookDao<GithubWebhookURI, GithubWebhook> {
    public GithubWebhookDao(EntityManager em) {
        super(GithubWebhook.class, GithubWebhook_.entityClassIRI, em);
    }
}
