package cz.lukaskabc.ontology.ontopus.core.rest.utils;

import cz.lukaskabc.ontology.ontopus.core_model.config.OntopusConfig;
import org.springframework.security.web.util.matcher.RequestMatcher;

import jakarta.servlet.http.HttpServletRequest;

public class SystemUriSecurityMatcher implements RequestMatcher {
    private final RequestUrlNotStartsWithCondition urlNotStartsWithSystemUri;
    private final RequestUrlNotStartsWithCondition urlNotStartsWithDcatUri;

    public SystemUriSecurityMatcher(OntopusConfig config) {
        this.urlNotStartsWithSystemUri = new RequestUrlNotStartsWithCondition(config.getSystemUri());
        this.urlNotStartsWithDcatUri =
                new RequestUrlNotStartsWithCondition(config.getDcatCatalog().getBaseUri());
    }

    @Override
    public boolean matches(HttpServletRequest request) {
        final boolean isSystemUrl = urlNotStartsWithSystemUri.getMatchingCondition(request) == null;
        final boolean isDcatUrl = urlNotStartsWithDcatUri.getMatchingCondition(request) == null;
        return isSystemUrl && !isDcatUrl;
    }
}
