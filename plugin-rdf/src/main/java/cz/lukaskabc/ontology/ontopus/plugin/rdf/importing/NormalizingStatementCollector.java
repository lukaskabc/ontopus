package cz.lukaskabc.ontology.ontopus.plugin.rdf.importing;

import cz.lukaskabc.ontology.ontopus.core_model.util.StringUtils;
import org.eclipse.rdf4j.model.*;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;
import org.eclipse.rdf4j.rio.helpers.StatementCollector;

import java.util.Collection;

public class NormalizingStatementCollector extends StatementCollector {
    /**
     * Creates a new StatementCollector that stores reported statements in the supplied collection and that uses a new
     * LinkedHashMap to store the reported namespaces.
     *
     * @param statements
     */
    public NormalizingStatementCollector(Collection<Statement> statements) {
        super(statements);
    }

    // TODO general normalization??

    @Override
    public void handleStatement(Statement st) {
        Statement normalized = SimpleValueFactory.getInstance()
                .createStatement(
                        (Resource) normalize(st.getSubject()),
                        (IRI) normalize(st.getPredicate()),
                        normalize(st.getObject()),
                        st.getContext());
        super.handleStatement(normalized);
    }

    private Value normalize(Value value) {
        switch (value.getType()) {
            case IRI -> {
                String iri = StringUtils.withoutTrailingSlash(value.stringValue());
                return SimpleValueFactory.getInstance().createIRI(iri);
            }
            case Triple -> {
                Triple triple = (Triple) value;
                Resource subject = (Resource) normalize(triple.getSubject());
                IRI predicate = (IRI) normalize(triple.getPredicate());
                Value object = normalize(triple.getObject());

                return SimpleValueFactory.getInstance().createTriple(subject, predicate, object);
            }
            default -> {
                return value;
            }
        }
    }
}
