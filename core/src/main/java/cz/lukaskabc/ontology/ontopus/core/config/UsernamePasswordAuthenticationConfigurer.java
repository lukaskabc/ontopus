package cz.lukaskabc.ontology.ontopus.core.config;

import cz.lukaskabc.ontology.ontopus.core.rest.LoginController;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.HttpSecurityBuilder;
import org.springframework.security.config.annotation.web.RequestMatcherFactory;
import org.springframework.security.config.annotation.web.configurers.AbstractAuthenticationFilterConfigurer;
import org.springframework.security.config.annotation.web.configurers.FormLoginConfigurer;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.ui.DefaultLoginPageGeneratingFilter;
import org.springframework.security.web.util.matcher.RequestMatcher;

/**
 * Basically alternative implementation to {@link FormLoginConfigurer}
 * but without the default login page ({@link DefaultLoginPageGeneratingFilter})
 *
 * @param <H>
 */
public class UsernamePasswordAuthenticationConfigurer<H extends HttpSecurityBuilder<H>> extends
        AbstractAuthenticationFilterConfigurer<H, FormLoginConfigurer<H>, UsernamePasswordAuthenticationFilter> {

    public UsernamePasswordAuthenticationConfigurer() {
        super(new UsernamePasswordAuthenticationFilter(), null);
        failureHandler(LoginController::onFailure);
        successHandler(LoginController::onSuccess);
    }

    /**
     * @see FormLoginConfigurer#createLoginProcessingUrlMatcher(String)
     */
    @Override
    protected RequestMatcher createLoginProcessingUrlMatcher(String loginProcessingUrl) {
        return RequestMatcherFactory.matcher(HttpMethod.POST, loginProcessingUrl);
    }

    @Override
    public void init(H http) throws Exception {
        super.init(http);
    }

}
