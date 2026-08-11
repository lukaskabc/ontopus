package cz.lukaskabc.ontology.ontopus.plugin.rdf.publishing;

import cz.lukaskabc.ontology.ontopus.api.model.DcatEntityRequest;
import cz.lukaskabc.ontology.ontopus.api.rest.StreamingResponseBody;
import cz.lukaskabc.ontology.ontopus.api.rest.UniversalDcatController;
import cz.lukaskabc.ontology.ontopus.core_model.config.OntopusConfig;
import cz.lukaskabc.ontology.ontopus.core_model.model.id.GraphURI;
import cz.lukaskabc.ontology.ontopus.core_model.model.id.GraphURIImpl;
import cz.lukaskabc.ontology.ontopus.core_model.model.id.ResourceURI;
import cz.lukaskabc.ontology.ontopus.core_model.model.ontology.PrefixDeclaration;
import cz.lukaskabc.ontology.ontopus.core_model.service.GraphService;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.eclipse.rdf4j.rio.RDFWriterFactory;
import org.eclipse.rdf4j.rio.RDFWriterRegistry;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;

import java.net.URI;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Controller
public class DcatRDFController implements UniversalDcatController {

    private static List<PrefixDeclaration> getPrefixDeclarations(OntopusConfig.DcatCatalog catalogConfig) {
        return catalogConfig.getPrefixDeclarations().entrySet().stream()
                .map(entry -> new PrefixDeclaration(entry.getKey(), URI.create(entry.getValue())))
                .sorted()
                .toList();
    }

    private final List<PrefixDeclaration> prefixDeclarations;

    private final GraphService graphService;

    public DcatRDFController(GraphService graphService, OntopusConfig ontopusConfig) {
        this.graphService = graphService;
        this.prefixDeclarations = getPrefixDeclarations(ontopusConfig.getDcatCatalog());
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
    public ResponseEntity<StreamingResponseBody> handleRequest(DcatEntityRequest<? extends ResourceURI> request) {
        final GraphURI graph = new GraphURIImpl(request.graph().toURI());
        return handleRequestWithData(request, () -> graphService.findAllWithSubject(graph, request.identifier()));
    }

    protected ResponseEntity<StreamingResponseBody> handleRequestWithData(
            DcatEntityRequest<?> request, RdfSupplier dataSupplier) {

        final RDFFormat rdfFormat = RdfFormatResolver.resolveRdfFormat(request.mediaType());
        final RDFWriterFactory writerFactory =
                RDFWriterRegistry.getInstance().get(rdfFormat).orElseThrow();
        return ResponseEntity.status(HttpStatus.OK)
                .contentType(MediaType.valueOf(rdfFormat.getDefaultMIMEType()))
                .body(new RdfResponseWriter(writerFactory, dataSupplier, prefixDeclarations));
    }
}
