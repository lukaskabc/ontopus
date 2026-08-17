package cz.lukaskabc.ontology.ontopus.tests.plugin.alias.filter;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import cz.lukaskabc.ontology.ontopus.api.service.core.MediaTypeResolver;
import cz.lukaskabc.ontology.ontopus.plugin.alias.filter.URIAliasFilter;
import cz.lukaskabc.ontology.ontopus.plugin.alias.persistence.service.AliasService;
import cz.lukaskabc.ontology.ontopus.tests.rest.MvcTestRunner;
import org.junit.jupiter.api.Test;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.Optional;

@WebMvcTest(controllers = URIAliasFilterTest.DummyController.class)
@Import({URIAliasFilter.class, URIAliasFilterTest.DummyController.class})
class URIAliasFilterTest extends MvcTestRunner {

    @MockitoBean
    private AliasService aliasService;

    @MockitoBean
    private MediaTypeResolver mediaTypeResolver;

    @Test
    void requestWithoutSuffixIsNotRedirectedWhenAliasDoesNotExist() throws Exception {
        final URI requested = URI.create("http://example.com/requested");

        when(mediaTypeResolver.resolveSuffixType(any())).thenReturn(Optional.empty());
        when(aliasService.findAliasFor(any())).thenReturn(Optional.empty());

        mockMvc.perform(get(requested))
                .andExpect(status().isOk())
                .andExpect(content().string("Success"));

        verify(aliasService).findAliasFor(requested);
    }

    @Test
    void requestWithoutTypeSuffixIsRedirectedToExistingAlias() throws Exception {
        final URI requested = URI.create("http://example.com/requested");
        final URI alias = URI.create("http://example.com/alias");

        when(mediaTypeResolver.resolveSuffixType(any())).thenReturn(Optional.empty());
        when(aliasService.findAliasFor(requested)).thenReturn(Optional.of(alias));

        mockMvc.perform(get(requested))
                .andExpect(status().isFound())
                .andExpect(header().string(HttpHeaders.LOCATION, alias.toString()));
    }

    @Test
    void requestWithSuffixIsNotRedirectedWhenAliasDoesNotExist() throws Exception {
        final URI requested = URI.create("http://example.com/requested.ttl");

        when(mediaTypeResolver.resolveSuffixType(any())).thenReturn(Optional.empty());
        when(aliasService.findAliasFor(any())).thenReturn(Optional.empty());

        mockMvc.perform(get(requested))
                .andExpect(status().isOk())
                .andExpect(content().string("Success"));

        verify(aliasService).findAliasFor(requested);
    }

    @Test
    void requestWithTypeSuffixIsRedirectedToAliasWithSuffix() throws Exception {
        final URI requested = URI.create("http://example.com/requested");
        final URI alias = URI.create("http://example.com/alias");

        when(mediaTypeResolver.resolveSuffixType(any())).thenReturn(Optional.of(MediaType.valueOf("text/turtle")));
        when(aliasService.findAliasFor(requested)).thenReturn(Optional.of(alias));

        final String suffix = ".ttl";
        final String requestedWithSuffix = requested + suffix;
        final String aliasWithSuffix = alias + suffix;

        mockMvc.perform(get(requestedWithSuffix))
                .andExpect(status().isFound())
                .andExpect(header().string(HttpHeaders.LOCATION, aliasWithSuffix));
    }

    @Test
    void requestWithUnknownSuffixTypeIsNotRedirectedWhenAliasExists() throws Exception {
        final URI requested = URI.create("http://example.com/requested");
        final URI requestedWithSuffix = URI.create(requested + ".myType");

        when(mediaTypeResolver.resolveSuffixType(any())).thenReturn(Optional.empty());
        when(aliasService.findAliasFor(requested)).thenReturn(Optional.of(URI.create("http://example.com/alias")));

        mockMvc.perform(get(requestedWithSuffix))
                .andExpect(status().isOk())
                .andExpect(content().string("Success"));

        verify(aliasService).findAliasFor(requestedWithSuffix);
        verifyNoMoreInteractions(aliasService);
    }

    @RestController
    static class DummyController {
        @GetMapping("/**")
        public String dummyEndpoint() {
            return "Success";
        }
    }
}
