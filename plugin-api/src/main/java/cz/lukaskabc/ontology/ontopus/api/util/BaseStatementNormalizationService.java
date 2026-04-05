package cz.lukaskabc.ontology.ontopus.api.util;

import org.eclipse.rdf4j.model.*;

/** Service capable of normalization of the RDF data. Defaults to no operation. */
public abstract class BaseStatementNormalizationService {
    protected final ValueFactory valueFactory;

    public BaseStatementNormalizationService(ValueFactory valueFactory) {
        this.valueFactory = valueFactory;
    }

    /**
     * Normalizes the given blank node
     *
     * @param bNode the blank node to normalize
     * @return Normalized blank node
     */
    protected BNode normalize(BNode bNode) {
        return bNode;
    }

    /**
     * Normalizes the given IRI
     *
     * @param iri the IRI to normalize
     * @return Normalized IRI
     */
    protected IRI normalize(IRI iri) {
        return iri;
    }

    /**
     * Normalizes the given literal
     *
     * @param literal the literal to normalize
     * @return Normalized literal
     */
    protected Literal normalize(Literal literal) {
        return literal;
    }

    /**
     * Normalizes the given resource
     *
     * @param resource the resource to normalize
     * @return Normalized resource
     */
    protected Resource normalize(Resource resource) {
        return (Resource) normalize((Value) resource);
    }

    public Statement normalize(Statement statement) {
        Resource subject = normalizeSubject(statement.getSubject());
        IRI predicate = normalizePredicate(statement.getPredicate());
        Value object = normalizeObject(statement.getObject());
        Resource context = normalizeContext(statement.getContext());
        if (subject == statement.getSubject()
                && predicate == statement.getPredicate()
                && object == statement.getObject()
                && context == statement.getContext()) {
            return statement;
        }
        return valueFactory.createStatement(subject, predicate, object, context);
    }

    /**
     * Normalizes the given triple
     *
     * @param triple the triple to normalize
     * @return Normalized triple
     */
    protected Triple normalize(Triple triple) {
        Resource subject = normalizeSubject(triple.getSubject());
        IRI predicate = normalizePredicate(triple.getPredicate());
        Value object = normalizeObject(triple.getObject());
        if (subject == triple.getSubject() && predicate == triple.getPredicate() && object == triple.getObject()) {
            return triple;
        }
        return valueFactory.createTriple(subject, predicate, object);
    }

    /**
     * Normalizes the given value
     *
     * @param value the value to normalize
     * @return Normalized value
     */
    protected Value normalize(Value value) {
        return switch (value.getType()) {
            case IRI -> normalize((IRI) value);
            case Triple -> normalize((Triple) value);
            case BNode -> normalize((BNode) value);
            case Literal -> normalize((Literal) value);
            default -> throw new IllegalArgumentException("Unsupported value type: " + value.getType());
        };
    }

    /**
     * Normalizes the given context
     *
     * @param context the context to normalize
     * @return Normalized context
     */
    protected Resource normalizeContext(Resource context) {
        return context;
    }

    /**
     * Normalizes the given URI from namespace statement
     *
     * @param uri the uri to normalize
     * @return Normalized URI
     */
    public String normalizeNamespaceUri(String uri) {
        return normalize(valueFactory.createIRI(uri)).stringValue();
    }

    /**
     * Normalizes the given object
     *
     * @param object the object to normalize
     * @return Normalized object
     */
    protected Value normalizeObject(Value object) {
        return normalize(object);
    }

    /**
     * Normalizes the given predicate
     *
     * @param predicate the predicate to normalize
     * @return Normalized predicate
     */
    protected IRI normalizePredicate(IRI predicate) {
        return normalize(predicate);
    }

    /**
     * Normalizes the given subject
     *
     * @param subject the subject to normalize
     * @return Normalized subject
     */
    protected Resource normalizeSubject(Resource subject) {
        return normalize(subject);
    }
}
