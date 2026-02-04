package cz.lukaskabc.ontology.ontopus.core_model.model;

import cz.cvut.kbss.jopa.model.annotations.ConstructorResult;
import cz.cvut.kbss.jopa.model.annotations.SparqlResultSetMapping;
import cz.cvut.kbss.jopa.model.annotations.VariableResult;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Resource;
import org.eclipse.rdf4j.model.Statement;
import org.eclipse.rdf4j.model.Value;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;

@SparqlResultSetMapping(
        name = Triple.MAPPING_NAME,
        classes = {
            @ConstructorResult(
                    targetClass = Triple.class,
                    variables = {
                        @VariableResult(name = "subject", type = String.class),
                        @VariableResult(name = "predicate", type = String.class),
                        @VariableResult(name = "object", type = String.class),
                        @VariableResult(name = "context", type = String.class),
                    })
        })
public class Triple implements Statement {
    public static final String MAPPING_NAME = "TripleResultMapping";
    private final Resource subject;
    private final IRI predicate;
    private final Value object;
    private final Resource context;

    public Triple(String subject, String predicate, String object, String context) {
        final SimpleValueFactory factory = SimpleValueFactory.getInstance();
        this.subject = factory.createIRI(subject);
        this.predicate = factory.createIRI(predicate);
        Value objectValue = null;
        try {
            objectValue = factory.createIRI(object);
        } catch (RuntimeException e) {
            objectValue = factory.createLiteral(object);
        }
        this.object = objectValue;
        this.context = factory.createIRI(context);
    }

    @Override
    public Resource getContext() {
        return context;
    }

    @Override
    public Value getObject() {
        return object;
    }

    @Override
    public IRI getPredicate() {
        return predicate;
    }

    @Override
    public Resource getSubject() {
        return subject;
    }
}
