package cz.lukaskabc.ontology.ontopus.api.service.core;

import org.eclipse.rdf4j.model.Statement;
import org.eclipse.rdf4j.rio.helpers.StatementCollector;

import java.util.Collection;

/**
 * Factory that creates a {@link StatementCollector} that normalizes all collected RDF data using all available
 * implementations of {@link cz.lukaskabc.ontology.ontopus.api.util.BaseStatementNormalizationService
 * BaseStatementNormalizationService}.
 */
public interface StatementNormalizationCollectorFactory {
    StatementCollector create(Collection<Statement> statements);
}
