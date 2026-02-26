package cz.lukaskabc.ontology.ontopus.core_model.persistence.repository;

import cz.lukaskabc.ontology.ontopus.core_model.model.id.GraphURI;
import cz.lukaskabc.ontology.ontopus.core_model.persistence.dao.GraphDao;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public class GraphRepository {
    private final GraphDao graphDao;

    public GraphRepository(GraphDao graphDao) {
        this.graphDao = graphDao;
    }

    /** @see GraphDao#copy(GraphURI, GraphURI) */
    @Transactional
    public void copy(GraphURI source, GraphURI target) {
        graphDao.copy(source, target);
    }

    /** @see GraphDao#move(GraphURI, GraphURI) */
    @Transactional
    public void move(GraphURI source, GraphURI target) {
        graphDao.move(source, target);
    }
}
