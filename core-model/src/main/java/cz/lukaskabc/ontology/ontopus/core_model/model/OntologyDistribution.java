package cz.lukaskabc.ontology.ontopus.core_model.model;

import cz.cvut.kbss.jopa.model.MultilingualString;
import cz.cvut.kbss.jopa.model.annotations.OWLClass;
import cz.cvut.kbss.jopa.model.annotations.OWLDataProperty;
import cz.lukaskabc.ontology.ontopus.core_model.generated.Vocabulary;
import cz.lukaskabc.ontology.ontopus.core_model.model.dcat.Distribution;
import cz.lukaskabc.ontology.ontopus.core_model.model.id.DistributionURI;
import jakarta.validation.constraints.NotNull;
import java.net.URL;
import java.time.Instant;
import org.springframework.util.MimeType;

/**
 * A specific representation of an {@link VersionArtifact}. An artifact might be available in multiple serializations
 * that may differ in various ways, including natural language, media-type or format, schematic organization, temporal
 * and spatial resolution, level of detail or profiles (which might specify any or all of the above).
 */
@OWLClass(iri = Vocabulary.s_c_dcat_Distribution)
public class OntologyDistribution extends Distribution<DistributionURI> {
    @NotNull @OWLDataProperty(iri = Vocabulary.s_p_dcat_identifier, simpleLiteral = true)
    private DistributionURI identifier;

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

    @Override
    public DistributionURI getIdentifier() {
        return identifier;
    }

    @Override
    public void setIdentifier(DistributionURI identifier) {
        this.identifier = identifier;
    }
}
