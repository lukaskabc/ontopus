package cz.lukaskabc.ontology.ontopus.plugin.git.persistence.service;

import cz.lukaskabc.ontology.ontopus.core_model.service.base.BaseService;
import cz.lukaskabc.ontology.ontopus.plugin.git.model.WebhookEntry;
import cz.lukaskabc.ontology.ontopus.plugin.git.persistence.identifier.WebhookEntryURI;
import cz.lukaskabc.ontology.ontopus.plugin.git.persistence.repository.WebhookEntryRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class WebhookEntryService extends BaseService<WebhookEntryURI, WebhookEntry, WebhookEntryRepository> {
    public WebhookEntryService(WebhookEntryRepository repository) {
        super(repository);
    }

    public List<WebhookEntry> findAll() {
        return repository.findAll();
    }
}
