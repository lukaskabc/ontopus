package cz.lukaskabc.ontology.ontopus.core.normalization;

import cz.lukaskabc.ontology.ontopus.api.util.BaseStatementNormalizationService;
import org.eclipse.rdf4j.model.Statement;
import org.eclipse.rdf4j.rio.RDFHandlerException;
import org.eclipse.rdf4j.rio.helpers.StatementCollector;

import java.util.Collection;
import java.util.List;

/** Normalizes every statement before collection with every normalization service */
public class StatementNormalizationCollector extends StatementCollector {
    private final List<BaseStatementNormalizationService> normalizationServices;

    public StatementNormalizationCollector(
            Collection<Statement> statements, List<BaseStatementNormalizationService> normalizationServices) {
        super(statements);
        this.normalizationServices = normalizationServices;
    }

    @Override
    public void handleComment(String comment) throws RDFHandlerException {
        super.handleComment(comment);
    }

    @Override
    public void handleNamespace(String prefix, String uri) throws RDFHandlerException {
        String normalizedUri = uri;
        for (BaseStatementNormalizationService service : normalizationServices) {
            normalizedUri = service.normalizeNamespaceUri(normalizedUri);
        }
        super.handleNamespace(prefix, normalizedUri);
    }

    @Override
    public void handleStatement(Statement st) {
        for (BaseStatementNormalizationService service : normalizationServices) {
            st = service.normalize(st);
        }
        super.handleStatement(st);
    }
}
