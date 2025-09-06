package cz.lukaskabc.ontology.ontopus.core.rest;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class LoginController {
    private static final Logger log = LogManager.getLogger(LoginController.class);

    public static void onFailure(
            HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) {
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
    }

    public static void onSuccess(
            HttpServletRequest request, HttpServletResponse httpServletResponse, Authentication authentication) {
        log.info("User {} logged in successfully", authentication.getName());
        httpServletResponse.setStatus(HttpServletResponse.SC_OK);
    }

    /**
     * Only for documentation purposes. The authentication is configured by
     * {@link cz.lukaskabc.ontology.ontopus.core.config.UsernamePasswordAuthenticationConfigurer
     * UsernamePasswordAuthenticationConfigurer}
     */
    @PostMapping(path = "login", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public void login(
            @RequestParam(name = "username") String username, @RequestParam(name = "password") String password) {}

    @GetMapping(path = "/auth-ping")
    public ResponseEntity<Void> ping() {
        return ResponseEntity.ok().build();
    }
}
