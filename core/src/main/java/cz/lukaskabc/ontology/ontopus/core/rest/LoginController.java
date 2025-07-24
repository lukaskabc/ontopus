package cz.lukaskabc.ontology.ontopus.core.rest;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Log4j2
@RestController
public class LoginController {

    /**
     * Only for documentation purposes.
     * The authentication is configured by
     * {@link cz.lukaskabc.ontology.ontopus.core.config.UsernamePasswordAuthenticationConfigurer UsernamePasswordAuthenticationConfigurer}
     */
    @PostMapping(path = "login", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public void login(@RequestParam(name = "username") String username, @RequestParam(name = "password") String password) {
    }


    public static void onSuccess(HttpServletRequest request, HttpServletResponse httpServletResponse, Authentication authentication) {
        log.info("User {} logged in successfully", authentication.getName());
        httpServletResponse.setStatus(HttpServletResponse.SC_OK);
    }

    public static void onFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) {
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
    }
}
