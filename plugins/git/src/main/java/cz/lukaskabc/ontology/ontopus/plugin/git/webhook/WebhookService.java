package cz.lukaskabc.ontology.ontopus.plugin.git.webhook;

import cz.lukaskabc.ontology.ontopus.core_model.model.id.TypedIdentifier;
import cz.lukaskabc.ontology.ontopus.core_model.model.id.VersionSeriesURI;
import cz.lukaskabc.ontology.ontopus.core_model.service.base.BaseService;
import cz.lukaskabc.ontology.ontopus.plugin.git.model.Webhook;
import cz.lukaskabc.ontology.ontopus.plugin.git.persistence.repository.WebhookRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;
import java.util.Optional;

public abstract class WebhookService<
                I extends TypedIdentifier, E extends Webhook<I>, R extends WebhookRepository<I, E, ?>>
        extends BaseService<I, E, R> {

    protected WebhookService(R repository) {
        super(repository);
    }

    @Transactional
    public void deleteByVersionSeries(VersionSeriesURI versionSeries) {
        E webhook = repository.findByVersionSeries(versionSeries).orElseThrow();
        I identifier = Objects.requireNonNull(webhook.getIdentifier(), "Webhook identifier must not be null");
        repository.deleteById(identifier);
    }

    public Optional<E> findByVersionSeries(VersionSeriesURI versionSeries) {
        return repository.findByVersionSeries(versionSeries);
    }
}
