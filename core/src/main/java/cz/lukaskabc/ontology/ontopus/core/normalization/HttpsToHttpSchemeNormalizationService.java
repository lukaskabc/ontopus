package cz.lukaskabc.ontology.ontopus.core.normalization;

import cz.lukaskabc.ontology.ontopus.api.util.BaseStatementNormalizationService;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

/** Transforms IRIs using {@code https://} scheme to http */
@Component
public class HttpsToHttpSchemeNormalizationService extends BaseStatementNormalizationService {

    public HttpsToHttpSchemeNormalizationService() {
        super(SimpleValueFactory.getInstance());
    }

    /**
     * Normalizes the given IRI
     *
     * @param iri the IRI to normalize
     * @return Normalized IRI
     */
    @Override
    protected IRI normalize(IRI iri) {
        final URI uri = URI.create(iri.stringValue());
        if ("https".equals(uri.getScheme())) {
            final String normalized =
                    UriComponentsBuilder.fromUri(uri).scheme("http").build().toUriString();
            return valueFactory.createIRI(normalized);
        }
        return iri;
    }
}
