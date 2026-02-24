package cz.lukaskabc.ontology.ontopus.core.rest.controller;

import cz.lukaskabc.ontology.ontopus.api.rest.OntologyController;
import cz.lukaskabc.ontology.ontopus.api.rest.OntopusRequest;
import cz.lukaskabc.ontology.ontopus.api.rest.ResourceController;
import cz.lukaskabc.ontology.ontopus.core.persistence.ContextDao;
import cz.lukaskabc.ontology.ontopus.core_model.exception.OntopusException;
import cz.lukaskabc.ontology.ontopus.core_model.model.Triple;
import org.eclipse.rdf4j.rio.*;
import org.eclipse.rdf4j.rio.helpers.BasicWriterSettings;
import org.jspecify.annotations.NullMarked;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.io.OutputStream;
import java.util.Collection;
import java.util.Optional;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@NullMarked
@Controller
public class DatabaseRDFController
        implements ResourceController<StreamingResponseBody>, OntologyController<StreamingResponseBody> {

    private final ContextDao contextDao;

    public DatabaseRDFController(ContextDao contextDao) {
        this.contextDao = contextDao;
    }

    private Optional<RDFFormat> findCompatible(MediaType mediaType) {
        return RDFWriterRegistry.getInstance().getKeys().stream()
                .filter(rdfFormat -> rdfFormat.getMIMETypes().stream()
                        .map(MediaType::valueOf)
                        // .filter(Predicate.not(MediaType.TEXT_PLAIN::isCompatibleWith))
                        .anyMatch(mediaType::isCompatibleWith))
                .findAny();
    }

    @Override
    public Set<MediaType> getSupportedMediaTypes() {
        return RDFWriterRegistry.getInstance().getKeys().stream()
                .map(RDFFormat::getMIMETypes)
                .flatMap(Collection::stream)
                .map(MediaType::valueOf)
                .collect(Collectors.toSet());
    }

    @Override
    public ResponseEntity<StreamingResponseBody> handleOntologyRequest(OntopusRequest requestContext) {
        return handleRequestWithData(requestContext, () -> contextDao.findAllTriples(requestContext.graphURI()));
    }

    private ResponseEntity<StreamingResponseBody> handleRequestWithData(
            OntopusRequest request, DataSupplier dataSupplier) {
        try {
            final RDFFormat rdfFormat = resolveRdfFormat(request.mediaType());
            final RDFWriterFactory writerFactory =
                    RDFWriterRegistry.getInstance().get(rdfFormat).orElseThrow();
            return ResponseEntity.status(HttpStatus.OK)
                    .contentType(MediaType.valueOf(rdfFormat.getDefaultMIMEType()))
                    .body(new ResponseWriter(writerFactory, dataSupplier));
        } catch (HttpMediaTypeNotAcceptableException e) {
            throw new OntopusException(e); // TODO: exception
        }
    }

    @Override
    public ResponseEntity<StreamingResponseBody> handleResourceRequest(OntopusRequest requestContext) {
        return handleRequestWithData(
                requestContext,
                () -> contextDao.findAllWithSubject(requestContext.graphURI(), requestContext.requestedURI()));
    }

    private RDFFormat resolveRdfFormat(MediaType mediaType) throws HttpMediaTypeNotAcceptableException {
        return findCompatible(mediaType)
                .orElseThrow(() -> new HttpMediaTypeNotAcceptableException(
                        "No compatible RDF format found for media type: " + mediaType));
    }

    private interface DataSupplier extends Supplier<Stream<Triple>> {}

    private static class ResponseWriter implements StreamingResponseBody {
        private final RDFWriterFactory writerFactory;
        private final DataSupplier dataSupplier;

        public ResponseWriter(RDFWriterFactory writerFactory, DataSupplier dataSupplier) {
            this.writerFactory = writerFactory;
            this.dataSupplier = dataSupplier;
        }

        @Transactional
        @Override
        public void writeTo(OutputStream outputStream) {
            RDFWriter writer = writerFactory.getWriter(outputStream);
            WriterConfig writerConfig = new WriterConfig().useDefaults();
            final boolean enablePrettyPrint = true;
            writerConfig.set(BasicWriterSettings.PRETTY_PRINT, enablePrettyPrint);
            // merge lines: (consumes memory!)
            writerConfig.set(BasicWriterSettings.INLINE_BLANK_NODES, enablePrettyPrint);
            writer.setWriterConfig(writerConfig);

            writer.startRDF();

            // TODO handle namespaces?
            dataSupplier.get().forEach(writer::handleStatement);
            writer.endRDF();
        }
    }
}
