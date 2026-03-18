package cz.lukaskabc.ontology.ontopus.plugin.git.github;

import cz.lukaskabc.ontology.ontopus.core_model.model.id.VersionSeriesURI;
import cz.lukaskabc.ontology.ontopus.core_model.persistence.repository.base.AbstractRepository;
import cz.lukaskabc.ontology.ontopus.core_model.util.StringUtils;
import cz.lukaskabc.ontology.ontopus.plugin.git.model.GithubWebhook;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.Validator;

import java.util.Optional;

@Repository
public class GithubWebhookRepository extends AbstractRepository<GithubWebhookURI, GithubWebhook, GithubWebhookDao> {

    public GithubWebhookRepository(GithubWebhookDao dao, Validator validator) {
        super(dao, validator, null);
    }

    @Transactional
    public Optional<GithubWebhook> findByVersionSeries(VersionSeriesURI versionSeries) {
        return Optional.ofNullable(dao.findByVersionSeries(versionSeries));
    }

    @Override
    protected <T> T validated(T entity) {
        if (entity instanceof GithubWebhook webhook && !StringUtils.hasText(webhook.getRef())) {
            webhook.setRef(null);
        }
        return super.validated(entity);
    }
}
