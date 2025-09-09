package cz.lukaskabc.ontology.ontopus.core.model.dcat;

import cz.cvut.kbss.jopa.model.MultilingualString;
import cz.cvut.kbss.jopa.model.annotations.MappedSuperclass;
import cz.cvut.kbss.jopa.model.annotations.OWLDataProperty;
import cz.lukaskabc.ontology.ontopus.core.generated.Vocabulary;
import cz.lukaskabc.ontology.ontopus.core.model.PersistenceEntity;
import cz.lukaskabc.ontology.ontopus.core.model.utils.DocumentedOWLClass;
import java.net.URL;
import java.time.Instant;
import org.springframework.util.MimeType;

/**
 * A specific representation of a dataset. A dataset might be available in multiple serializations that may differ in
 * various ways, including natural language, media-type or format, schematic organization, temporal and spatial
 * resolution, level of detail or profiles (which might specify any or all of the above).
 */
@MappedSuperclass
@DocumentedOWLClass(iri = Vocabulary.s_c_dcat_Distribution)
public abstract class Distribution extends PersistenceEntity {
    @OWLDataProperty(iri = Vocabulary.s_p_dcat_title)
    private MultilingualString title;

    @OWLDataProperty(iri = Vocabulary.s_p_dcat_description)
    private MultilingualString description;

    @OWLDataProperty(iri = Vocabulary.s_p_dcat_issued)
    private Instant releaseDate;

    @OWLDataProperty(iri = Vocabulary.s_p_dcat_modified)
    private Instant modifiedDate;
    /*
     * Skipping license, access right, rights, has policy
     */
    @OWLDataProperty(iri = Vocabulary.s_p_dcat_accessURL)
    private URL accessURL;
    /*
     * Skipping access service
     */
    @OWLDataProperty(iri = Vocabulary.s_p_dcat_downloadURL)
    private URL downloadURL;

    @OWLDataProperty(iri = Vocabulary.s_p_dcat_byteSize)
    private Long byteSize;
    /*
     * Skipping spatial resolution in meters, temporal resolution, conforms to
     */
    @OWLDataProperty(iri = Vocabulary.s_p_dcat_mediaType)
    private MimeType mediaType;

    @OWLDataProperty(iri = Vocabulary.s_p_dcat_format)
    private String format;

    @OWLDataProperty(iri = Vocabulary.s_p_dcat_compressFormat)
    private MimeType compressFormat;

    @OWLDataProperty(iri = Vocabulary.s_p_dcat_compressFormat)
    private MimeType packageFormat;
    // skipping checksum

    public Distribution(
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

    public URL getAccessURL() {
        return accessURL;
    }

    public Long getByteSize() {
        return byteSize;
    }

    public MimeType getCompressFormat() {
        return compressFormat;
    }

    public MultilingualString getDescription() {
        return description;
    }

    public URL getDownloadURL() {
        return downloadURL;
    }

    public String getFormat() {
        return format;
    }

    public MimeType getMediaType() {
        return mediaType;
    }

    public Instant getModifiedDate() {
        return modifiedDate;
    }

    public MimeType getPackageFormat() {
        return packageFormat;
    }

    public Instant getReleaseDate() {
        return releaseDate;
    }

    public MultilingualString getTitle() {
        return title;
    }
}
