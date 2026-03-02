package cz.lukaskabc.ontology.ontopus.core_model.persistence.repository;

import cz.lukaskabc.ontology.ontopus.core_model.model.TemporaryContext;
import cz.lukaskabc.ontology.ontopus.core_model.model.id.TemporaryContextURI;
import cz.lukaskabc.ontology.ontopus.core_model.persistence.dao.GraphDao;
import cz.lukaskabc.ontology.ontopus.core_model.persistence.dao.TemporaryContextDao;
import cz.lukaskabc.ontology.ontopus.core_model.persistence.identifier.IdentifierGenerator;
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
            IdentifierGenerator<TemporaryContextURI, TemporaryContext> identifierGenerator,
            GraphDao graphDao) {
        super(dao, validator, identifierGenerator);
        this.graphDao = graphDao;
    }

    @Override
    @Transactional
    public void delete(TemporaryContext context) {
        Objects.requireNonNull(context.getIdentifier(), "Context identifier cannot be null");
        super.delete(context);
        graphDao.delete(context.getIdentifier());
    }

    @Transactional
    public void deleteAll() {
        dao.findAll().forEach(this::delete);
    }
}
