package cz.lukaskabc.ontology.ontopus.core.model;

import cz.cvut.kbss.jopa.model.MultilingualString;
import cz.cvut.kbss.jopa.model.annotations.OWLClass;
import cz.cvut.kbss.jopa.model.annotations.OWLDataProperty;
import cz.lukaskabc.ontology.ontopus.core.generated.Vocabulary;
import org.springframework.util.MimeType;

import java.net.URL;
import java.time.Instant;

/**
 * A specific representation of an {@link OntologyArtifact}.
 */
@OWLClass(iri = Vocabulary.s_c_OntologyDistribution)
public abstract class OntologyDistribution extends PersistenceEntity {
    @OWLDataProperty(iri = Vocabulary.s_p_dcat_title)
    private final MultilingualString title;
    @OWLDataProperty(iri = Vocabulary.s_p_dcat_description)
    private final MultilingualString description;
    @OWLDataProperty(iri = Vocabulary.s_p_dcat_issued)
    private final Instant releaseDate;
    @OWLDataProperty(iri = Vocabulary.s_p_dcat_modified)
    private final Instant modifiedDate;
    /*
    Skipping license, access right, rights, has policy
     */
    @OWLDataProperty(iri = Vocabulary.s_p_dcat_accessURL)
    private final URL accessURL;
    /*
    Skipping access service
     */
    @OWLDataProperty(iri = Vocabulary.s_p_dcat_downloadURL)
    private final URL downloadURL;
    @OWLDataProperty(iri = Vocabulary.s_p_dcat_byteSize)
    private final Long byteSize;
    /*
    Skipping spatial resolution in meters, temporal resolution, conforms to
     */
    @OWLDataProperty(iri = Vocabulary.s_p_dcat_mediaType)
    private final MimeType mediaType;
    @OWLDataProperty(iri = Vocabulary.s_p_dcat_format)
    private final String format;
    @OWLDataProperty(iri = Vocabulary.s_p_dcat_compressFormat)
    private final MimeType compressFormat;
    @OWLDataProperty(iri = Vocabulary.s_p_dcat_compressFormat)
    private final MimeType packageFormat;
    // skipping checksum (not present in the actual ontology https://www.w3.org/ns/dcat3.ttl)


    public OntologyDistribution(MultilingualString title, MultilingualString description, Instant releaseDate, Instant modifiedDate, URL accessURL, URL downloadURL, Long byteSize, MimeType mediaType, String format, MimeType compressFormat, MimeType packageFormat) {
        this.title = title;
        this.description = description;
        this.releaseDate = releaseDate;
        this.modifiedDate = modifiedDate;
        this.accessURL = accessURL;
        this.downloadURL = downloadURL;
        this.byteSize = byteSize;
        this.mediaType = mediaType;
        this.format = format;
        this.compressFormat = compressFormat;
        this.packageFormat = packageFormat;
    }

    public MultilingualString getTitle() {
        return title;
    }

    public MultilingualString getDescription() {
        return description;
    }

    public Instant getReleaseDate() {
        return releaseDate;
    }

    public Instant getModifiedDate() {
        return modifiedDate;
    }

    public URL getAccessURL() {
        return accessURL;
    }

    public URL getDownloadURL() {
        return downloadURL;
    }

    public Long getByteSize() {
        return byteSize;
    }

    public MimeType getMediaType() {
        return mediaType;
    }

    public String getFormat() {
        return format;
    }

    public MimeType getCompressFormat() {
        return compressFormat;
    }

    public MimeType getPackageFormat() {
        return packageFormat;
    }
}
