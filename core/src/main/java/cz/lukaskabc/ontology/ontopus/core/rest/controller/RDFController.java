package cz.lukaskabc.ontology.ontopus.core.rest.controller;

import cz.lukaskabc.ontology.ontopus.core.persistence.ContextDao;
import cz.lukaskabc.ontology.ontopus.core_model.model.Triple;
import org.eclipse.rdf4j.rio.*;
import org.eclipse.rdf4j.rio.helpers.BasicWriterSettings;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.accept.ContentNegotiationManager;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import jakarta.servlet.http.HttpServletRequest;
import java.io.OutputStream;
import java.net.URI;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Stream;

@Controller
public class RDFController {
    private static final RDFFormat FALLBACK_FORMAT = RDFFormat.TURTLE;
    private final ContextDao contextDao;
    private final ContentNegotiationManager contentNegotiationManager;

    public RDFController(ContextDao contextDao, ContentNegotiationManager contentNegotiationManager) {
        this.contextDao = contextDao;
        this.contentNegotiationManager = contentNegotiationManager;
    }

    @Nullable private RDFFormat findCompatible(MediaType mediaType) {
        return RDFWriterRegistry.getInstance().getKeys().stream()
                .filter(rdfFormat -> rdfFormat.getMIMETypes().stream()
                        .map(MediaType::valueOf)
                        .filter(Predicate.not(MediaType.TEXT_PLAIN::isCompatibleWith))
                        .anyMatch(mediaType::isCompatibleWith))
                .findAny()
                .orElse(null);
    }

    // @GetMapping("/**")
    public ResponseEntity<@NonNull StreamingResponseBody> resolve(ServletWebRequest webRequest) throws Exception {
        final HttpServletRequest request = webRequest.getRequest();
        final String decodedUri = URLDecoder.decode(request.getRequestURL().toString(), StandardCharsets.UTF_8);
        final URI requestURI = new URI(decodedUri);
        final RDFFormat rdfFormat = resolveRdfFormat(webRequest);
        final MediaType mediaType = MediaType.valueOf(rdfFormat.getDefaultMIMEType());
        final RDFWriterFactory writerFactory =
                RDFWriterRegistry.getInstance().get(rdfFormat).orElseThrow();
        final StreamingResponseBody body = new ResponseWriter(requestURI, writerFactory, contextDao);
        return ResponseEntity.status(HttpStatus.OK).contentType(mediaType).body(body);
    }

    private RDFFormat resolveRdfFormat(NativeWebRequest request) throws HttpMediaTypeNotAcceptableException {
        return contentNegotiationManager.resolveMediaTypes(request).stream()
                .map(this::findCompatible)
                .filter(Objects::nonNull)
                .findFirst()
                .orElse(FALLBACK_FORMAT);
    }

    private static class ResponseWriter implements StreamingResponseBody {
        private final RDFWriterFactory writerFactory;
        private final ContextDao contextDao;
        private final URI contextURI;

        private ResponseWriter(URI contextURI, RDFWriterFactory writerFactory, ContextDao contextDao) {
            this.contextURI = contextURI;
            this.writerFactory = writerFactory;
            this.contextDao = contextDao;
        }

        @Transactional
        @Override
        public void writeTo(@NonNull OutputStream outputStream) {
            Stream<Triple> triples = contextDao.findAllTriples(contextURI);
            RDFWriter writer = writerFactory.getWriter(outputStream);
            WriterConfig writerConfig = new WriterConfig().useDefaults();
            final boolean enablePrettyPrint = true;
            writerConfig.set(BasicWriterSettings.PRETTY_PRINT, enablePrettyPrint);
            // merge lines: (consumes memory!)
            writerConfig.set(BasicWriterSettings.INLINE_BLANK_NODES, enablePrettyPrint);
            writer.setWriterConfig(writerConfig);

            writer.startRDF();

            // TODO handle namespaces?
            triples.forEach(writer::handleStatement);
            writer.endRDF();
        }
    }
}
