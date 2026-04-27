package cz.lukaskabc.ontology.ontopus.core_model.model.ontology;

import cz.cvut.kbss.jopa.model.MultilingualString;
import cz.cvut.kbss.jopa.model.annotations.OWLClass;
import cz.cvut.kbss.jopa.model.annotations.Types;
import cz.lukaskabc.ontology.ontopus.core_model.generated.Vocabulary;
import cz.lukaskabc.ontology.ontopus.core_model.model.dcat.Distribution;
import cz.lukaskabc.ontology.ontopus.core_model.model.id.DistributionURI;
import cz.lukaskabc.ontology.ontopus.core_model.model.util.MappedClassTypesResolver;
import org.springframework.util.MimeType;

import java.net.URI;
import java.net.URL;
import java.time.Instant;
import java.util.Set;

/**
 * A specific representation of an {@link VersionArtifact}. An artifact might be available in multiple serializations
 * that may differ in various ways, including natural language, media-type or format, schematic organization, temporal
 * and spatial resolution, level of detail or profiles (which might specify any or all of the above).
 */
@OWLClass(iri = Vocabulary.s_c_dcat_Distribution)
public class OntologyDistribution extends Distribution<DistributionURI> {
    public static final Set<URI> TYPES = MappedClassTypesResolver.resolveTypes(OntologyDistribution.class);

    @Types
    private Set<URI> types = TYPES;

    public OntologyDistribution() {
        super();
    }

    public OntologyDistribution(
            MultilingualString title,
            MultilingualString description,
            Instant releaseDate,
            Instant modifiedDate,
            URL accessURL,
            URL downloadURL,
            Long byteSize,
            MimeType mediaType,
            String format,
            MimeType compressFormat,
            MimeType packageFormat) {
        super(
                title,
                description,
                releaseDate,
                modifiedDate,
                accessURL,
                downloadURL,
                byteSize,
                mediaType,
                format,
                compressFormat,
                packageFormat);
    }

    public Set<URI> getTypes() {
        return types;
    }

    @Override
    protected DistributionURI wrapUri(URI uri) {
        return new DistributionURI(uri);
    }
}
