package cz.lukaskabc.ontology.ontopus.core_model.persistence.dao;

import cz.cvut.kbss.jopa.model.EntityManager;
import cz.lukaskabc.ontology.ontopus.core_model.model.id.OntopusCatalogURI;
import cz.lukaskabc.ontology.ontopus.core_model.model.ontology.OntopusCatalog;
import cz.lukaskabc.ontology.ontopus.core_model.model.ontology.OntopusCatalog_;
import cz.lukaskabc.ontology.ontopus.core_model.persistence.dao.base.AbstractDao;
import org.eclipse.rdf4j.model.Statement;
import org.eclipse.rdf4j.model.ValueFactory;
import org.eclipse.rdf4j.query.TupleQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class OntopusCatalogDao extends AbstractDao<OntopusCatalogURI, OntopusCatalog> {
    @Autowired
    public OntopusCatalogDao(EntityManager em) {

        super(OntopusCatalog.class, OntopusCatalog_.entityClassIRI, em);
    }

    /** Finds all triples in the catalog graph */
    public List<Statement> findAllTriples() {
        return runSelectQuery(conn -> {
            TupleQuery query = conn.prepareTupleQuery("""
					SELECT ?s ?p ?o WHERE {
					    GRAPH ?graph {
					        ?s ?p ?o .
					    }
					} ORDER BY ?s ?p ?o
					""");

            ValueFactory vf = conn.getValueFactory();
            query.setBinding("graph", vf.createIRI(entityGraphContext.toString()));

            return query;
        });
    }
}
