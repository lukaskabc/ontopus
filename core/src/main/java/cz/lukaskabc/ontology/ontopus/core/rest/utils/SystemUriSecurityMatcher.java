package cz.lukaskabc.ontology.ontopus.core.rest.utils;

import cz.lukaskabc.ontology.ontopus.core_model.config.OntopusConfig;
import org.springframework.security.web.util.matcher.RequestMatcher;

import jakarta.servlet.http.HttpServletRequest;

public class SystemUriSecurityMatcher implements RequestMatcher {
    private final RequestUrlNotStartsWithCondition requestUrlNotStartsWithCondition;

    public SystemUriSecurityMatcher(OntopusConfig config) {
        this.requestUrlNotStartsWithCondition = new RequestUrlNotStartsWithCondition(config.getSystemURI());
    }

    @Override
    public boolean matches(HttpServletRequest request) {
        return !request.getServletPath().startsWith("/admin")
                && requestUrlNotStartsWithCondition.getMatchingCondition(request) == null;
    }
}
