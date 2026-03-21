package cz.lukaskabc.ontology.ontopus.core.rest.utils;

import cz.lukaskabc.ontology.ontopus.core_model.config.OntopusConfig;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.web.util.matcher.RequestMatcher;

import jakarta.servlet.http.HttpServletRequest;

public class SystemUriSecurityMatcher implements RequestMatcher {
    private static final Logger log = LogManager.getLogger(SystemUriSecurityMatcher.class);
    private final RequestUrlNotStartsWithCondition requestUrlNotStartsWithCondition;

    public SystemUriSecurityMatcher(OntopusConfig config) {
        this.requestUrlNotStartsWithCondition = new RequestUrlNotStartsWithCondition(config.getSystemURI());
    }

    @Override
    public boolean matches(HttpServletRequest request) {
        final boolean matches = !request.getServletPath().startsWith("/admin")
                && requestUrlNotStartsWithCondition.getMatchingCondition(request) == null;

        log.trace("Request to {} matches: {}", request.getServletPath(), matches);

        return matches;
    }
}
