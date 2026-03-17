package cz.lukaskabc.ontology.ontopus.plugin.webhook.persistence.dao;

import cz.cvut.kbss.jopa.model.EntityManager;
import cz.cvut.kbss.jopa.model.descriptors.EntityDescriptor;
import cz.lukaskabc.ontology.ontopus.core_model.exception.PersistenceException;
import cz.lukaskabc.ontology.ontopus.core_model.model.id.VersionSeriesURI;
import cz.lukaskabc.ontology.ontopus.core_model.persistence.dao.base.AbstractDao;
import cz.lukaskabc.ontology.ontopus.plugin.git.model.WebhookEntry_;
import cz.lukaskabc.ontology.ontopus.plugin.webhook.model.WebhookEntry;
import cz.lukaskabc.ontology.ontopus.plugin.webhook.persistence.identifier.WebhookEntryURI;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class WebhookEntryDao extends AbstractDao<WebhookEntryURI, WebhookEntry> {
    public WebhookEntryDao(EntityManager em) {
        super(
                WebhookEntry.class,
                WebhookEntry_.entityClassIRI,
                em,
                new EntityDescriptor(WebhookEntry_.entityClassIRI.toURI()));
    }

    public List<WebhookEntry> findAll(VersionSeriesURI seriesIdentifier) {
        try {
            return em.createQuery(
                            "SELECT e FROM " + WebhookEntry.class.getSimpleName()
                                    + " e WHERE e.versionSeries = :series",
                            WebhookEntry.class)
                    .setParameter("series", seriesIdentifier.toURI())
                    .setDescriptor(descriptor)
                    .getResultList();
        } catch (Exception e) {
            throw new PersistenceException("Failed to retrieve all WebhookEntry entities.", e);
        }
    }
}
