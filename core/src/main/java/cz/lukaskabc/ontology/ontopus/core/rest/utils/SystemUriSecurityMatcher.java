package cz.lukaskabc.ontology.ontopus.core.rest.utils;

import cz.lukaskabc.ontology.ontopus.core_model.config.OntopusConfig;
import org.springframework.security.web.util.matcher.RequestMatcher;

import jakarta.servlet.http.HttpServletRequest;

public class SystemUriSecurityMatcher implements RequestMatcher {
    private final RequestUrlStartsWith urlStartsWithSystemUri;
    private final RequestUrlStartsWith urlStartsWithDcatUri;

    public SystemUriSecurityMatcher(OntopusConfig config) {
        this.urlStartsWithSystemUri = new RequestUrlStartsWith(config.getSystemUri());
        this.urlStartsWithDcatUri =
                new RequestUrlStartsWith(config.getDcatCatalog().getBaseUri());
    }

    @Override
    public boolean matches(HttpServletRequest request) {
        final boolean isSystemUrl = urlStartsWithSystemUri.getMatchingCondition(request) != null;
        final boolean isDcatUrl = urlStartsWithDcatUri.getMatchingCondition(request) != null;
        return isSystemUrl && !isDcatUrl;
    }
}
