package cz.lukaskabc.ontology.ontopus.plugin.git.persistence.repository;

import cz.lukaskabc.ontology.ontopus.core_model.config.OntopusConfig;
import cz.lukaskabc.ontology.ontopus.core_model.model.id.TypedIdentifier;
import cz.lukaskabc.ontology.ontopus.core_model.model.id.VersionSeriesURI;
import cz.lukaskabc.ontology.ontopus.core_model.persistence.repository.base.AbstractRepository;
import cz.lukaskabc.ontology.ontopus.plugin.git.model.Webhook;
import cz.lukaskabc.ontology.ontopus.plugin.git.persistence.dao.WebhookDao;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.Validator;

import java.util.Optional;

public abstract class WebhookRepository<I extends TypedIdentifier, E extends Webhook<I>, D extends WebhookDao<I, E>>
        extends AbstractRepository<I, E, D> {

    protected WebhookRepository(D dao, Validator validator, OntopusConfig config) {
        super(dao, validator, null, config);
    }

    @Transactional(readOnly = true)
    public Optional<E> findByVersionSeries(VersionSeriesURI versionSeries) {
        return Optional.ofNullable(dao.findByVersionSeries(versionSeries));
    }
}
