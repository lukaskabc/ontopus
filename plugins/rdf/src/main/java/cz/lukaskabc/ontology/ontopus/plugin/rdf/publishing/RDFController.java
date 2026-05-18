package cz.lukaskabc.ontology.ontopus.plugin.rdf.publishing;

import cz.lukaskabc.ontology.ontopus.api.rest.OntologyController;
import cz.lukaskabc.ontology.ontopus.api.rest.OntopusRequest;
import cz.lukaskabc.ontology.ontopus.api.rest.ResourceController;
import cz.lukaskabc.ontology.ontopus.api.rest.StreamingResponseBody;
import cz.lukaskabc.ontology.ontopus.core_model.model.id.GraphURI;
import cz.lukaskabc.ontology.ontopus.core_model.model.id.ResourceURI;
import cz.lukaskabc.ontology.ontopus.core_model.model.ontology.PrefixDeclaration;
import cz.lukaskabc.ontology.ontopus.core_model.service.GraphService;
import cz.lukaskabc.ontology.ontopus.core_model.service.VersionArtifactService;
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
public class RDFController
        implements ResourceController<StreamingResponseBody>, OntologyController<StreamingResponseBody> {
    private final GraphService graphService;
    private final VersionArtifactService versionArtifactService;

    public RDFController(GraphService graphService, VersionArtifactService versionArtifactService) {
        this.graphService = graphService;
        this.versionArtifactService = versionArtifactService;
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
        final GraphURI graph = requestContext.ontologyVersionUri();
        return handleRequestWithData(requestContext, () -> graphService.findAllTriples(graph));
    }

    private ResponseEntity<StreamingResponseBody> handleRequestWithData(
            OntopusRequest request, RdfSupplier dataSupplier) {
        final List<PrefixDeclaration> namespaces =
                versionArtifactService.findPrefixDeclarations(request.ontologyVersionUri());

        final RDFFormat rdfFormat = RdfFormatResolver.resolveRdfFormat(request.mediaType());
        final RDFWriterFactory writerFactory =
                RDFWriterRegistry.getInstance().get(rdfFormat).orElseThrow();
        return ResponseEntity.status(HttpStatus.OK)
                .contentType(MediaType.valueOf(rdfFormat.getDefaultMIMEType()))
                .body(new RdfResponseWriter(writerFactory, dataSupplier, namespaces));
    }

    @Override
    public ResponseEntity<StreamingResponseBody> handleResourceRequest(OntopusRequest requestContext) {
        final GraphURI graph = requestContext.ontologyVersionUri();
        final ResourceURI resource = requestContext.requestedURI();
        return handleRequestWithData(requestContext, () -> graphService.findAllWithSubject(graph, resource));
    }
}
