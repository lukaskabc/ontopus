package cz.lukaskabc.ontology.ontopus.plugin.git.persistence.repository;

import cz.lukaskabc.ontology.ontopus.core_model.config.OntopusConfig;
import cz.lukaskabc.ontology.ontopus.core_model.model.id.VersionSeriesURI;
import cz.lukaskabc.ontology.ontopus.core_model.persistence.repository.base.AbstractRepository;
import cz.lukaskabc.ontology.ontopus.core_model.util.StringUtils;
import cz.lukaskabc.ontology.ontopus.plugin.git.github.GithubWebhookURI;
import cz.lukaskabc.ontology.ontopus.plugin.git.model.GithubWebhook;
import cz.lukaskabc.ontology.ontopus.plugin.git.persistence.dao.GithubWebhookDao;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.Validator;

import java.util.Optional;

@Repository
public class GithubWebhookRepository extends AbstractRepository<GithubWebhookURI, GithubWebhook, GithubWebhookDao> {

    public GithubWebhookRepository(GithubWebhookDao dao, Validator validator, OntopusConfig config) {
        super(dao, validator, null, config);
    }

    @Transactional
    public Optional<GithubWebhook> findByVersionSeries(VersionSeriesURI versionSeries) {
        return Optional.ofNullable(dao.findByVersionSeries(versionSeries));
    }

    private boolean patternIsEmpty(GithubWebhook webhook) {
        return webhook.getRef() != null && !StringUtils.hasText(webhook.getRef().pattern());
    }

    @Override
    protected <T> @NonNull T validated(@NonNull T entity) {
        if (entity instanceof GithubWebhook webhook && patternIsEmpty(webhook)) {
            webhook.setRef(null);
        }
        return super.validated(entity);
    }
}
