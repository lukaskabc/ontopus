package cz.lukaskabc.ontology.ontopus.core.normalization;

import cz.lukaskabc.ontology.ontopus.api.service.core.StatementNormalizationCollectorFactory;
import cz.lukaskabc.ontology.ontopus.api.util.BaseStatementNormalizationService;
import org.eclipse.rdf4j.model.Statement;
import org.eclipse.rdf4j.rio.helpers.StatementCollector;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;

@Component
public class StatementNormalizationCollectorFactoryImpl implements StatementNormalizationCollectorFactory {
    private final List<BaseStatementNormalizationService> normalizationServices;

    public StatementNormalizationCollectorFactoryImpl(List<BaseStatementNormalizationService> normalizationServices) {
        this.normalizationServices = normalizationServices;
    }

    @Override
    public StatementCollector create(Collection<Statement> statements) {
        return new StatementNormalizationCollector(statements, normalizationServices);
    }
}
