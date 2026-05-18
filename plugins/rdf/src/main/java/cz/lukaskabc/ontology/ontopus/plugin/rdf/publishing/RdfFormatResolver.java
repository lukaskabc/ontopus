package cz.lukaskabc.ontology.ontopus.plugin.rdf.publishing;

import org.eclipse.rdf4j.rio.RDFFormat;
import org.eclipse.rdf4j.rio.RDFWriterRegistry;
import org.springframework.http.MediaType;

import java.util.Optional;

public class RdfFormatResolver {
    public static Optional<RDFFormat> findCompatible(MediaType mediaType) {
        return RDFWriterRegistry.getInstance().getKeys().stream()
                .filter(rdfFormat -> rdfFormat.getMIMETypes().stream()
                        .map(MediaType::valueOf)
                        // .filter(Predicate.not(MediaType.TEXT_PLAIN::isCompatibleWith))
                        .anyMatch(mediaType::isCompatibleWith))
                .findAny();
    }
    /**
     * Resolve a compatible RDF format for the given media type.
     *
     * @param mediaType the media type to resolve
     * @return a compatible RDF format
     * @throws IllegalStateException if no compatible RDF format is found
     */
    public static RDFFormat resolveRdfFormat(MediaType mediaType) {
        return findCompatible(mediaType)
                // throwing indicates that the configuration of OntoPuS changed and previously
                // supported format is not supported anymore
                .orElseThrow(
                        () -> new IllegalStateException("No compatible RDF format found for media type: " + mediaType));
    }

    private RdfFormatResolver() {
        throw new AssertionError();
    }
}
