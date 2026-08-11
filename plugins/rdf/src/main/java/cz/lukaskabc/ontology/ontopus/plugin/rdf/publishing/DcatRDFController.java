package cz.lukaskabc.ontology.ontopus.plugin.rdf.publishing;

import cz.lukaskabc.ontology.ontopus.api.model.DcatEntityRequest;
import cz.lukaskabc.ontology.ontopus.api.rest.CatalogController;
import cz.lukaskabc.ontology.ontopus.api.rest.StreamingResponseBody;
import cz.lukaskabc.ontology.ontopus.api.rest.VersionArtifactController;
import cz.lukaskabc.ontology.ontopus.api.rest.VersionSeriesController;
import cz.lukaskabc.ontology.ontopus.core_model.config.OntopusConfig;
import cz.lukaskabc.ontology.ontopus.core_model.model.id.*;
import cz.lukaskabc.ontology.ontopus.core_model.model.ontology.PrefixDeclaration;
import cz.lukaskabc.ontology.ontopus.core_model.service.CatalogService;
import cz.lukaskabc.ontology.ontopus.core_model.service.GraphService;
import cz.lukaskabc.ontology.ontopus.core_model.service.VersionArtifactService;
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
public class DcatRDFController implements CatalogController, VersionSeriesController, VersionArtifactController {

    private static List<PrefixDeclaration> getPrefixDeclarations(OntopusConfig.DcatCatalog catalogConfig) {
        return catalogConfig.getPrefixDeclarations().entrySet().stream()
                .map(entry -> new PrefixDeclaration(entry.getKey(), URI.create(entry.getValue())))
                .sorted()
                .toList();
    }

    private final CatalogService catalogService;

    private final List<PrefixDeclaration> prefixDeclarations;

    private final GraphService graphService;
    private final VersionArtifactService artifactService;

    public DcatRDFController(
            GraphService graphService,
            VersionArtifactService artifactService,
            OntopusConfig ontopusConfig,
            CatalogService catalogService) {
        this.graphService = graphService;
        this.artifactService = artifactService;
        this.prefixDeclarations = getPrefixDeclarations(ontopusConfig.getDcatCatalog());
        this.catalogService = catalogService;
    }

    @Override
    public ResponseEntity<StreamingResponseBody> getCatalog(DcatEntityRequest<OntopusCatalogURI> request) {
        return handleRequestWithData(request, catalogService::findAllTriples);
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
    public ResponseEntity<StreamingResponseBody> getVersionArtifact(DcatEntityRequest<VersionArtifactURI> request) {
        return handleRequestWithData(request, () -> artifactService.findAllTriples(request.identifier()));
    }

    @Override
    public ResponseEntity<StreamingResponseBody> getVersionSeries(DcatEntityRequest<VersionSeriesURI> request) {
        return handleRequest(request);
    }

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
