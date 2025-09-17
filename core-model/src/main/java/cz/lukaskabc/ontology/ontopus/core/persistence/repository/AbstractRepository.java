package cz.lukaskabc.ontology.ontopus.core.persistence.repository;

import cz.lukaskabc.ontology.ontopus.core.model.PersistenceEntity;
import cz.lukaskabc.ontology.ontopus.core.persistence.dao.AbstractDao;

public abstract class AbstractRepository<E extends PersistenceEntity, D extends AbstractDao<E>> {
    private final D dao;

    public AbstractRepository(D dao) {
        this.dao = dao;
    }
}
