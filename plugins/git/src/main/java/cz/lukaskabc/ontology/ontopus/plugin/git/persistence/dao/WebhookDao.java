package cz.lukaskabc.ontology.ontopus.plugin.git.persistence.dao;

import cz.cvut.kbss.jopa.model.EntityManager;
import cz.cvut.kbss.jopa.model.IRI;
import cz.lukaskabc.ontology.ontopus.core_model.model.id.TypedIdentifier;
import cz.lukaskabc.ontology.ontopus.core_model.model.id.VersionSeriesURI;
import cz.lukaskabc.ontology.ontopus.core_model.persistence.dao.base.AbstractDao;
import cz.lukaskabc.ontology.ontopus.plugin.git.model.Webhook;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jspecify.annotations.Nullable;

public abstract class WebhookDao<I extends TypedIdentifier, E extends Webhook<I>> extends AbstractDao<I, E> {
    private static final Logger log = LogManager.getLogger(WebhookDao.class);

    public WebhookDao(Class<E> entityClass, IRI typeUri, EntityManager em) {
        super(entityClass, typeUri, em);
    }

    @Nullable public E findByVersionSeries(VersionSeriesURI versionSeries) {
        try {
            return resultOrNull(em.createQuery(
                            "SELECT w FROM " + entityClass.getSimpleName() + " w WHERE w.versionSeries = :series",
                            entityClass)
                    .setParameter("series", versionSeries.toURI())
                    .setMaxResults(1)::getSingleResult);
        } catch (Exception e) {
            throw persistenceException(log, "Failed to find Webhook by version series", e);
        }
    }
}
