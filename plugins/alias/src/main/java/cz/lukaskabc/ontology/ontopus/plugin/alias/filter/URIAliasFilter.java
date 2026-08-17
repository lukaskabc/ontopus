package cz.lukaskabc.ontology.ontopus.plugin.alias.filter;

import cz.lukaskabc.ontology.ontopus.api.service.core.MediaTypeResolver;
import cz.lukaskabc.ontology.ontopus.core_model.util.StringUtils;
import cz.lukaskabc.ontology.ontopus.plugin.alias.persistence.service.AliasService;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.UriComponentsBuilder;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URI;
import java.util.Optional;

/** @see org.springframework.web.filter.UrlHandlerFilter */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class URIAliasFilter extends OncePerRequestFilter {
    private final AliasService aliasService;
    private final MediaTypeResolver mediaTypeResolver;

    public URIAliasFilter(AliasService aliasService, MediaTypeResolver mediaTypeResolver) {
        this.aliasService = aliasService;
        this.mediaTypeResolver = mediaTypeResolver;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        URI requested = URI.create(request.getRequestURL().toString());

        final Optional<String> fileSuffix = getFileSuffix(requested);

        if (fileSuffix.isPresent()) {
            requested = StringUtils.withoutSuffix(requested);
        }

        final Optional<URI> alias = aliasService.findAliasFor(requested);
        if (alias.isEmpty()) {
            // No alias found
            filterChain.doFilter(request, response);
            return;
        }

        // append fileSuffix to the alias if there was a suffix
        final URI redirectDestination =
                fileSuffix.map(suffix -> withFileSuffix(alias.get(), suffix)).orElseGet(alias::get);

        redirectToAlias(response, redirectDestination);
    }

    /**
     * Resolves the file extension from the URI.
     *
     * @param requestedURI the URI to resolve the file extension for
     * @return the resolved file extension if a valid media type was resolved from it, {@code null} otherwise.
     */
    protected Optional<String> getFileSuffix(URI requestedURI) {
        Optional<MediaType> suffixMediaType = mediaTypeResolver.resolveSuffixType(requestedURI);
        if (suffixMediaType.isEmpty()) {
            return Optional.empty();
        }

        final String fileExtension = StringUtils.getFilenameExtension(requestedURI.toString());
        return Optional.ofNullable(fileExtension).filter(StringUtils::hasText);
    }

    protected void redirectToAlias(HttpServletResponse response, URI alias) throws IOException {
        final String location = alias.toString();

        response.resetBuffer();
        response.setStatus(HttpStatus.FOUND.value());
        response.setHeader(HttpHeaders.LOCATION, location);
        response.flushBuffer();
    }

    protected URI withFileSuffix(URI uri, String suffix) {
        final String path = StringUtils.withoutTrailingSlash(uri.getPath());
        return UriComponentsBuilder.fromUri(uri)
                .replacePath(path)
                .path(".")
                .path(suffix)
                .build()
                .toUri();
    }
}
