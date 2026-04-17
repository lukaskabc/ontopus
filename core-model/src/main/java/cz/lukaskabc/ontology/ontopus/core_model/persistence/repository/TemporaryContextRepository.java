package cz.lukaskabc.ontology.ontopus.core_model.persistence.repository;

import cz.lukaskabc.ontology.ontopus.core_model.config.OntopusConfig;
import cz.lukaskabc.ontology.ontopus.core_model.model.TemporaryContext;
import cz.lukaskabc.ontology.ontopus.core_model.model.id.TemporaryContextURI;
import cz.lukaskabc.ontology.ontopus.core_model.persistence.dao.GraphDao;
import cz.lukaskabc.ontology.ontopus.core_model.persistence.dao.TemporaryContextDao;
import cz.lukaskabc.ontology.ontopus.core_model.persistence.identifier.TemporaryContextUriGenerator;
import cz.lukaskabc.ontology.ontopus.core_model.persistence.repository.base.AbstractRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.Validator;

import java.util.Objects;

@Repository
public class TemporaryContextRepository
        extends AbstractRepository<TemporaryContextURI, TemporaryContext, TemporaryContextDao> {
    private final GraphDao graphDao;

    public TemporaryContextRepository(
            TemporaryContextDao dao,
            Validator validator,
            TemporaryContextUriGenerator identifierGenerator,
            GraphDao graphDao,
            OntopusConfig config) {
        super(dao, validator, identifierGenerator, config);
        this.graphDao = graphDao;
    }

    @Override
    @Transactional
    public void delete(TemporaryContext context) {
        Objects.requireNonNull(context.getIdentifier(), "Context identifier cannot be null");
        delete(context.getIdentifier());
    }

    @Transactional
    public void delete(TemporaryContextURI identifier) {
        Objects.requireNonNull(identifier, "Context identifier cannot be null");
        dao.delete(identifier);
        graphDao.delete(identifier);
    }

    @Transactional
    public void deleteAll() {
        dao.findAll().forEach(this::delete);
    }

    /**
     * Persists a new entity, setting its identifier if it is missing.
     *
     * @param entity the entity to persist
     */
    @Override
    @Transactional
    public void persist(TemporaryContext entity) {
        Objects.requireNonNull(entity);
        setIdentifierIfMissing(entity);
        Objects.requireNonNull(entity.getIdentifier(), "Failed to generate temporary context identifier");
        graphDao.delete(entity.getIdentifier());
        dao.persist(validated(entity));
    }
}
