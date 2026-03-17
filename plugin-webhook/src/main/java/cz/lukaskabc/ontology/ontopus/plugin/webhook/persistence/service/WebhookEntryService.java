package cz.lukaskabc.ontology.ontopus.plugin.webhook.persistence.service;

import cz.lukaskabc.ontology.ontopus.core_model.model.id.VersionSeriesURI;
import cz.lukaskabc.ontology.ontopus.core_model.service.base.BaseService;
import cz.lukaskabc.ontology.ontopus.plugin.webhook.model.WebhookEntry;
import cz.lukaskabc.ontology.ontopus.plugin.webhook.persistence.identifier.WebhookEntryURI;
import cz.lukaskabc.ontology.ontopus.plugin.webhook.persistence.repository.WebhookEntryRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class WebhookEntryService extends BaseService<WebhookEntryURI, WebhookEntry, WebhookEntryRepository> {
    public WebhookEntryService(WebhookEntryRepository repository) {
        super(repository);
    }

    public List<WebhookEntry> findAll(VersionSeriesURI seriesIdentifier) {
        return repository.findAll(seriesIdentifier);
    }
}
