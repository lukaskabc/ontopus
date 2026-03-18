package cz.lukaskabc.ontology.ontopus.plugin.git.github;

import cz.cvut.kbss.jopa.model.EntityManager;
import cz.cvut.kbss.jopa.model.IRI;
import cz.cvut.kbss.jopa.model.descriptors.EntityDescriptor;
import cz.lukaskabc.ontology.ontopus.core_model.persistence.dao.base.AbstractDao;
import org.springframework.stereotype.Component;

import java.net.URI;

@Component
public class GithubWebhookDao extends AbstractDao<GithubWebhookURI, GithubWebhook> {
    public GithubWebhookDao(EntityManager em) {
        super(GithubWebhook.class, IRI.create(""), em, new EntityDescriptor(URI.create("")));
    }
}
