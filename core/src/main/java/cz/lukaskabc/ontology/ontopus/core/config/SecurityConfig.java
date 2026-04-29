package cz.lukaskabc.ontology.ontopus.core.config;

import cz.lukaskabc.ontology.ontopus.core.rest.utils.SystemUriSecurityMatcher;
import cz.lukaskabc.ontology.ontopus.core_model.config.OntopusConfig;
import cz.lukaskabc.ontology.ontopus.core_model.service.UserService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.annotation.web.configurers.LogoutConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.header.writers.DelegatingRequestMatcherHeaderWriter;
import org.springframework.security.web.header.writers.frameoptions.XFrameOptionsHeaderWriter;
import org.springframework.security.web.servlet.util.matcher.PathPatternRequestMatcher;
import org.springframework.security.web.session.HttpSessionEventPublisher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.net.URI;
import java.util.List;

@Configuration
public class SecurityConfig {
    private static void publicHeaderCustomizer(HeadersConfigurer<HttpSecurity> configurer) {
        // allow X-Frame-Options for public URIs
        configurer.addHeaderWriter(new DelegatingRequestMatcherHeaderWriter(
                PathPatternRequestMatcher.pathPattern("/public/**"),
                new XFrameOptionsHeaderWriter(XFrameOptionsHeaderWriter.XFrameOptionsMode.SAMEORIGIN)));
    }

    @Bean
    @Order(Ordered.HIGHEST_PRECEDENCE)
    public SecurityFilterChain administrationSecurityFilter(
            HttpSecurity http, AuthenticationManager authenticationManager, OntopusConfig config) {
        http.securityMatcher(new SystemUriSecurityMatcher(config))
                .csrf(AbstractHttpConfigurer::disable)
                .cors(Customizer.withDefaults())
                .httpBasic(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .authenticationManager(authenticationManager)
                .logout(LogoutConfigurer::permitAll)
                .headers(SecurityConfig::publicHeaderCustomizer)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED))
                .with(new UsernamePasswordAuthenticationConfigurer<>())
                .authorizeHttpRequests(auth -> auth.requestMatchers("/login")
                        .permitAll()
                        .requestMatchers("/admin/**")
                        .permitAll()
                        .requestMatchers("/public/**")
                        .permitAll()
                        .anyRequest()
                        .authenticated());
        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(UserService userService, PasswordEncoder passwordEncoder) {
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider(userService);
        authenticationProvider.setPasswordEncoder(passwordEncoder);

        return new ProviderManager(authenticationProvider);
    }

    @Bean
    UrlBasedCorsConfigurationSource corsConfigurationSource(OntopusConfig config) {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(config.getAdministrationAllowedOrigins().stream()
                .map(URI::toString)
                .toList());
        configuration.setAllowCredentials(true);
        configuration.addExposedHeader("Location");
        configuration.setAllowedMethods(List.of("GET", "POST", "OPTIONS", "DELETE", "PUT"));
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public HttpSessionEventPublisher httpSessionEventPublisher() {
        return new HttpSessionEventPublisher();
    }

    @Bean
    public SecurityFilterChain ontopusDefaultSecurityFilterChain(HttpSecurity http) {
        CorsConfiguration corsConfig = new CorsConfiguration();
        corsConfig.setAllowedOrigins(List.of("*")); // Allow all origins
        corsConfig.setAllowedMethods(List.of("GET", "HEAD", "OPTIONS"));
        corsConfig.setAllowedHeaders(List.of("*"));

        http.csrf(AbstractHttpConfigurer::disable) // disable csrf
                .httpBasic(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .cors(cors -> cors.configurationSource(request -> corsConfig))
                .headers(headers -> headers.frameOptions(HeadersConfigurer.FrameOptionsConfig::sameOrigin))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth.anyRequest().permitAll());
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
