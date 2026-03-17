package cz.lukaskabc.ontology.ontopus.plugin.webhook.persistence.repository;

import cz.lukaskabc.ontology.ontopus.core_model.model.id.VersionSeriesURI;
import cz.lukaskabc.ontology.ontopus.core_model.persistence.repository.base.AbstractRepository;
import cz.lukaskabc.ontology.ontopus.plugin.webhook.model.WebhookEntry;
import cz.lukaskabc.ontology.ontopus.plugin.webhook.persistence.dao.WebhookEntryDao;
import cz.lukaskabc.ontology.ontopus.plugin.webhook.persistence.identifier.WebhookEntryURI;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.Validator;

import java.util.List;

@Repository
public class WebhookEntryRepository extends AbstractRepository<WebhookEntryURI, WebhookEntry, WebhookEntryDao> {

    public WebhookEntryRepository(WebhookEntryDao dao, Validator validator) {
        super(dao, validator, null);
    }

    @Transactional(readOnly = true)
    public List<WebhookEntry> findAll(VersionSeriesURI seriesIdentifier) {
        return dao.findAll(seriesIdentifier);
    }
}
