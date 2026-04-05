package cz.lukaskabc.ontology.ontopus.core.normalization;

import cz.lukaskabc.ontology.ontopus.api.util.BaseStatementNormalizationService;
import cz.lukaskabc.ontology.ontopus.core_model.util.StringUtils;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;
import org.springframework.stereotype.Component;

/** Removes trailing slash(es) from IRIs */
@Component
public class TrailingSlashNormalizationService extends BaseStatementNormalizationService {
    public TrailingSlashNormalizationService() {
        super(SimpleValueFactory.getInstance());
    }

    @Override
    protected IRI normalize(IRI iri) {
        String normalized = StringUtils.withoutTrailingSlash(iri.stringValue());
        return SimpleValueFactory.getInstance().createIRI(normalized);
    }
}
