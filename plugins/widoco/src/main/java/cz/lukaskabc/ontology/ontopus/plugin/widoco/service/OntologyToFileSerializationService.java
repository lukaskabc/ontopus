package cz.lukaskabc.ontology.ontopus.plugin.widoco.service;

import cz.lukaskabc.ontology.ontopus.core_model.exception.InternalException;
import cz.lukaskabc.ontology.ontopus.core_model.exception.OntopusException;
import cz.lukaskabc.ontology.ontopus.core_model.generated.Vocabulary;
import cz.lukaskabc.ontology.ontopus.core_model.model.id.GraphURI;
import cz.lukaskabc.ontology.ontopus.core_model.model.ontology.PrefixDeclaration;
import cz.lukaskabc.ontology.ontopus.core_model.service.GraphService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.rdf4j.rio.*;
import org.eclipse.rdf4j.rio.helpers.BasicWriterSettings;
import org.springframework.stereotype.Service;

import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;

@Service
public class OntologyToFileSerializationService {
    private static final Logger log = LogManager.getLogger(OntologyToFileSerializationService.class);
    private static final RDFWriterFactory turtleWriterFactory = RDFWriterRegistry.getInstance()
            .get(RDFFormat.TURTLE)
            .orElseThrow(() -> new IllegalStateException("Turtle RDF format not supported"));
    private final WriterConfig writerConfig;
    private final GraphService graphService;

    public OntologyToFileSerializationService(GraphService graphService) {
        this.writerConfig = new WriterConfig().useDefaults();
        writerConfig.set(BasicWriterSettings.PRETTY_PRINT, false);
        writerConfig.set(BasicWriterSettings.INLINE_BLANK_NODES, true);
        this.graphService = graphService;
    }

    public void serializeOntologyToFile(Collection<PrefixDeclaration> namespaces, GraphURI graph, Path filePath) {
        try (OutputStream outputStream = Files.newOutputStream(filePath)) {
            RDFWriter writer = turtleWriterFactory.getWriter(outputStream);
            writer.setWriterConfig(writerConfig);
            writer.startRDF();
            namespaces.forEach(ns -> writer.handleNamespace(ns.getPrefix(), ns.getName()));
            graphService.findAllTriples(graph).forEach(writer::handleStatement);
            writer.endRDF();
        } catch (Exception e) {
            throw log.throwing(InternalException.builder()
                    .errorType(Vocabulary.u_i_serialization)
                    .internalMessage("Failed to serialize ontology to file")
                    .detailMessageArguments(OntopusException.EMPTY_ARGUMENTS)
                    .titleMessageCode("ontopus.plugin.widoco.error.serializationFailure")
                    .cause(e)
                    .build());
        }
    }
}
