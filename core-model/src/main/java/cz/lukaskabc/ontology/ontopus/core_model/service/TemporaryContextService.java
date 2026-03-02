package cz.lukaskabc.ontology.ontopus.core_model.service;

import cz.lukaskabc.ontology.ontopus.core_model.model.TemporaryContext;
import cz.lukaskabc.ontology.ontopus.core_model.model.id.TemporaryContextURI;
import cz.lukaskabc.ontology.ontopus.core_model.persistence.repository.TemporaryContextRepository;
import cz.lukaskabc.ontology.ontopus.core_model.service.base.BaseService;
import cz.lukaskabc.ontology.ontopus.core_model.util.TimeProvider;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

@Service
public class TemporaryContextService
        extends BaseService<TemporaryContextURI, TemporaryContext, TemporaryContextRepository> {
    private static final Logger log = LogManager.getLogger(TemporaryContextService.class);
    private final TimeProvider timeProvider;

    public TemporaryContextService(TemporaryContextRepository repository, TimeProvider timeProvider) {
        super(repository);
        this.timeProvider = timeProvider;
    }

    public void deleteAll() {
        log.debug("Deleting all temporary contexts");
        repository.deleteAll();
    }

    public TemporaryContext generate() {
        final TemporaryContext context = new TemporaryContext();
        context.setCreatedAt(timeProvider.getInstant());
        repository.persist(context);
        log.debug("Generated temporary context with identifier <{}>", context.getIdentifier());
        return context;
    }
}
