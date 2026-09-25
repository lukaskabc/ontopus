package cz.lukaskabc.ontology.ontopus.plugin.git.persistence.repository;

import cz.lukaskabc.ontology.ontopus.core_model.config.OntopusConfig;
import cz.lukaskabc.ontology.ontopus.core_model.model.id.TypedIdentifier;
import cz.lukaskabc.ontology.ontopus.core_model.util.StringUtils;
import cz.lukaskabc.ontology.ontopus.plugin.git.model.GitWebhook;
import cz.lukaskabc.ontology.ontopus.plugin.git.persistence.dao.WebhookDao;
import org.jspecify.annotations.NonNull;
import org.springframework.validation.Validator;

public abstract class GitWebhookRepository<
                I extends TypedIdentifier, E extends GitWebhook<I>, D extends WebhookDao<I, E>>
        extends WebhookRepository<I, E, D> {

    protected GitWebhookRepository(D dao, Validator validator, OntopusConfig config) {
        super(dao, validator, config);
    }

    @Override
    protected <T> @NonNull T validated(@NonNull T entity) {
        if (entity instanceof GitWebhook<?> webhook
                && webhook.getRef() != null
                && !StringUtils.hasText(webhook.getRef().pattern())) {
            webhook.setRef(null);
        }
        return super.validated(entity);
    }
}
