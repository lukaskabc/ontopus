package cz.lukaskabc.ontology.ontopus.plugin.rdf.publishing;

import cz.cvut.kbss.jopa.model.IRI;
import cz.lukaskabc.ontology.ontopus.api.model.DcatEntityRequest;
import cz.lukaskabc.ontology.ontopus.api.rest.*;
import cz.lukaskabc.ontology.ontopus.core_model.model.id.*;
import cz.lukaskabc.ontology.ontopus.core_model.model.ontology.OntopusCatalog_;
import cz.lukaskabc.ontology.ontopus.core_model.model.ontology.PrefixDeclaration;
import cz.lukaskabc.ontology.ontopus.core_model.model.ontology.VersionArtifact_;
import cz.lukaskabc.ontology.ontopus.core_model.model.ontology.VersionSeries_;
import cz.lukaskabc.ontology.ontopus.core_model.service.GraphService;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.eclipse.rdf4j.rio.RDFWriterFactory;
import org.eclipse.rdf4j.rio.RDFWriterRegistry;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Controller
public class DcatRDFController
        implements CatalogController, VersionSeriesController, VersionArtifactController, DistributionController {

    private final GraphService graphService;

    public DcatRDFController(GraphService graphService) {
        this.graphService = graphService;
    }

    @Override
    public ResponseEntity<StreamingResponseBody> getCatalog(DcatEntityRequest<OntopusCatalogURI> request) {
        return handleRequest(request, OntopusCatalog_.entityClassIRI);
    }

    @Override
    public ResponseEntity<StreamingResponseBody> getDistribution(DcatEntityRequest<DistributionURI> request) {
        // return handleRequest(request, Distribution_.entityClassIRI);
        throw new UnsupportedOperationException("Distributions are not implemented");
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
        return handleRequest(request, VersionArtifact_.entityClassIRI);
    }

    @Override
    public ResponseEntity<StreamingResponseBody> getVersionSeries(DcatEntityRequest<VersionSeriesURI> request) {
        return handleRequest(request, VersionSeries_.entityClassIRI);
    }

    protected ResponseEntity<StreamingResponseBody> handleRequest(
            DcatEntityRequest<? extends ResourceURI> request, IRI graphURI) {
        final GraphURI graph = new GraphURIImpl(graphURI.toURI());
        return handleRequestWithData(request, () -> graphService.findAllWithSubject(graph, request.identifier()));
    }

    protected ResponseEntity<StreamingResponseBody> handleRequestWithData(
            DcatEntityRequest<?> request, RdfSupplier dataSupplier) {
        // TODO: handle namespaces
        final List<PrefixDeclaration> namespaces = List.of();
        // versionArtifactService.findPrefixDeclarations(request.ontologyVersionUri());

        final RDFFormat rdfFormat = RdfFormatResolver.resolveRdfFormat(request.mediaType());
        final RDFWriterFactory writerFactory =
                RDFWriterRegistry.getInstance().get(rdfFormat).orElseThrow();
        return ResponseEntity.status(HttpStatus.OK)
                .contentType(MediaType.valueOf(rdfFormat.getDefaultMIMEType()))
                .body(new RdfResponseWriter(writerFactory, dataSupplier, namespaces));
    }
}
