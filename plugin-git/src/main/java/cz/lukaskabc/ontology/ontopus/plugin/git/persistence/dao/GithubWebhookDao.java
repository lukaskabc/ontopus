package cz.lukaskabc.ontology.ontopus.plugin.git.persistence.dao;

import cz.cvut.kbss.jopa.model.EntityManager;
import cz.cvut.kbss.jopa.model.descriptors.EntityDescriptor;
import cz.lukaskabc.ontology.ontopus.core_model.model.id.VersionSeriesURI;
import cz.lukaskabc.ontology.ontopus.core_model.persistence.dao.base.AbstractDao;
import cz.lukaskabc.ontology.ontopus.plugin.git.github.GithubWebhookURI;
import cz.lukaskabc.ontology.ontopus.plugin.git.model.GithubWebhook;
import cz.lukaskabc.ontology.ontopus.plugin.git.model.GithubWebhook_;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jspecify.annotations.Nullable;
import org.springframework.stereotype.Component;

@Component
public class GithubWebhookDao extends AbstractDao<GithubWebhookURI, GithubWebhook> {
    private static final Logger log = LogManager.getLogger(GithubWebhookDao.class);

    public GithubWebhookDao(EntityManager em) {
        super(
                GithubWebhook.class,
                GithubWebhook_.entityClassIRI,
                em,
                new EntityDescriptor(GithubWebhook_.entityClassIRI.toURI()));
    }

    @Nullable public GithubWebhook findByVersionSeries(VersionSeriesURI versionSeries) {
        try {
            return resultOrNull(em.createQuery(
                            "SELECT w FROM " + entityClass.getSimpleName() + " w WHERE w.versionSeries = :series",
                            entityClass)
                    .setParameter("series", versionSeries.toURI())
                    .setMaxResults(1)::getSingleResult);
        } catch (Exception e) {
            throw persistenceException(log, "Failed to find GitHub Webhook by version series", e);
        }
    }
}
