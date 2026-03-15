package cz.lukaskabc.ontology.ontopus.plugin.git.persistence.repository;

import cz.lukaskabc.ontology.ontopus.core_model.persistence.repository.base.AbstractRepository;
import cz.lukaskabc.ontology.ontopus.plugin.git.model.WebhookEntry;
import cz.lukaskabc.ontology.ontopus.plugin.git.persistence.dao.WebhookEntryDao;
import cz.lukaskabc.ontology.ontopus.plugin.git.persistence.identifier.WebhookEntryURI;
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
    public List<WebhookEntry> findAll() {
        return dao.findAll();
    }
}
