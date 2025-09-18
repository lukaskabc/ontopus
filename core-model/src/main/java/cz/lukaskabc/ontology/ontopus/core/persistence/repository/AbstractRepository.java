package cz.lukaskabc.ontology.ontopus.core.persistence.repository;

import cz.lukaskabc.ontology.ontopus.core.model.PersistenceEntity;
import cz.lukaskabc.ontology.ontopus.core.model.id.EntityIdentifier;
import cz.lukaskabc.ontology.ontopus.core.persistence.dao.AbstractDao;

public abstract class AbstractRepository<
        I extends EntityIdentifier, E extends PersistenceEntity<I>, D extends AbstractDao<I, E>> {
    protected final D dao;

    public AbstractRepository(D dao) {
        this.dao = dao;
    }
}
