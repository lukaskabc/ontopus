package cz.lukaskabc.ontology.ontopus.tests.plugin.git.persistence.dao;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import cz.lukaskabc.ontology.ontopus.core_model.generated.Vocabulary;
import cz.lukaskabc.ontology.ontopus.plugin.git.model.GithubWebhook;
import cz.lukaskabc.ontology.ontopus.plugin.git.model.GithubWebhook_;
import cz.lukaskabc.ontology.ontopus.plugin.git.model.github.GithubEvent;
import cz.lukaskabc.ontology.ontopus.plugin.git.persistence.dao.GithubWebhookDao;
import cz.lukaskabc.ontology.ontopus.tests.persistence.BaseDaoTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;

import java.net.URI;

@Import(GithubWebhookDao.class)
public class GithubWebhookDaoTest extends BaseDaoTest {
    @Autowired
    GithubWebhookDao dao;

    @Test
    void githubWebhookIsPersistedInWebhookContext() {
        GithubWebhook githubWebhook = new GithubWebhook();
        githubWebhook.setEvent(GithubEvent.PUSH);

        transactional(() -> dao.persist(githubWebhook));
        assertNotNull(githubWebhook.getIdentifier());

        final URI context = readOnlyTransactional(em.createNativeQuery("""
				    SELECT ?graph WHERE {
				        GRAPH ?graph {
				            ?x a ?githubWebhook .
				        }
				    }
				""", URI.class)
                .setParameter("x", githubWebhook.getIdentifier().toURI())
                .setParameter("githubWebhook", GithubWebhook_.entityClassIRI)::getSingleResult);

        assertEquals(Vocabulary.u_c_ontopus_Webhook, context);
    }
}
