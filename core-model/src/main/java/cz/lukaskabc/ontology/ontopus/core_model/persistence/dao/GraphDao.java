package cz.lukaskabc.ontology.ontopus.core_model.persistence.dao;

import cz.cvut.kbss.jopa.model.EntityManager;
import cz.lukaskabc.ontology.ontopus.core_model.exception.PersistenceException;
import cz.lukaskabc.ontology.ontopus.core_model.model.id.GraphURI;
import org.springframework.stereotype.Component;

@Component
public class GraphDao {
    private final EntityManager em;

    public GraphDao(EntityManager em) {
        this.em = em;
    }

    /**
     * Copies the content of the source graph to the target graph. If the target graph already exists, it will be
     * dropped before copying as per SPARQL standard.
     *
     * @param source the graph to copy from
     * @param target the graph to copy to
     */
    public void copy(GraphURI source, GraphURI target) {
        try {
            em.createNativeQuery("""
					COPY GRAPH ?source TO ?target
					""")
                    .setParameter("source", source.toURI())
                    .setParameter("target", target.toURI())
                    .executeUpdate();
        } catch (Exception e) {
            throw new PersistenceException("Failed to copy graph from " + source + " to " + target, e);
        }
    }

    /**
     * Moves the content of the source graph to the target graph. If the target graph already exists, it will be dropped
     * before moving as per SPARQL standard.
     *
     * @param source the graph to move from
     * @param target the graph to move to
     */
    public void move(GraphURI source, GraphURI target) {
        try {
            em.createNativeQuery("""
					MOVE GRAPH ?source TO ?target
					""")
                    .setParameter("source", source.toURI())
                    .setParameter("target", target.toURI())
                    .executeUpdate();
        } catch (Exception e) {
            throw new PersistenceException("Failed to move graph from " + source + " to " + target, e);
        }
    }
}
