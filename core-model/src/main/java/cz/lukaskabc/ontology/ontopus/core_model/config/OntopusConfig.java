package cz.lukaskabc.ontology.ontopus.core_model.config;

import cz.lukaskabc.ontology.ontopus.core_model.model.id.OntopusCatalogURI;
import org.jspecify.annotations.NullUnmarked;
import org.jspecify.annotations.Nullable;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.io.File;
import java.net.URI;
import java.nio.file.Path;
import java.time.Duration;
import java.util.List;

@Validated
@ConfigurationProperties(prefix = "ontopus")
public class OntopusConfig {

    /**
     * The URI dedicated for the OntoPuS. Base paths are not supported. Usually you want to keep HTTP protocol and
     * replace the domain with dedicated subdomain for the server. Example: {@code http://example.com}
     */
    @NotNull private URI systemUri = URI.create("http://localhost");

    /**
     * Allowed origins for administration endpoints
     *
     * @configurationdoc.default the SYSTEM_URI value is used
     */
    @NotEmpty private List<URI> administrationAllowedOrigins = List.of(systemUri);

    @Nullable private File frontendIndexFile;

    private int defaultMaxPageSize = 100;

    @Valid private Database database = new Database();

    @Valid private DcatCatalog dcatCatalog = new DcatCatalog();

    @Valid private Files files = new Files();

    @Valid private Resource resource = new Resource();

    public List<URI> getAdministrationAllowedOrigins() {
        return administrationAllowedOrigins;
    }

    public Database getDatabase() {
        return database;
    }

    public DcatCatalog getDcatCatalog() {
        return dcatCatalog;
    }

    public int getDefaultMaxPageSize() {
        return defaultMaxPageSize;
    }

    public Files getFiles() {
        return files;
    }

    public @Nullable File getFrontendIndexFile() {
        return frontendIndexFile;
    }

    public Resource getResource() {
        return resource;
    }

    public URI getSystemUri() {
        return systemUri;
    }

    public void setAdministrationAllowedOrigins(List<URI> administrationAllowedOrigins) {
        this.administrationAllowedOrigins = administrationAllowedOrigins;
    }

    public void setDatabase(Database database) {
        this.database = database;
    }

    public void setDcatCatalog(DcatCatalog dcatCatalog) {
        this.dcatCatalog = dcatCatalog;
    }

    public OntopusConfig setDefaultMaxPageSize(int defaultMaxPageSize) {
        this.defaultMaxPageSize = defaultMaxPageSize;
        return this;
    }

    public void setFiles(Files files) {
        this.files = files;
    }

    public void setFrontendIndexFile(File frontendIndexFile) {
        this.frontendIndexFile = frontendIndexFile;
    }

    public void setResource(Resource resource) {
        this.resource = resource;
    }

    public void setSystemUri(URI systemUri) {
        this.systemUri = systemUri;
    }

    public static class Database {
        /**
         * JOPA OntoDriver implementation class
         *
         * @see <a href="https://github.com/kbss-cvut/jopa/wiki/OntoDriver">JOPA OntoDriver</a>
         */
        @NotNull private String driver = "cz.cvut.kbss.ontodriver.rdf4j.Rdf4jDataSource";

        /** The default persistence language */
        @NotNull private String language = "en";

        /** Username for authentication with database repository */
        @Nullable private String username;

        /** Password for authentication with database repository */
        @Nullable private String password;

        /** Database repository URL */
        @NotNull private String url = "http://localhost:7200/repositories/ontopus";

        public String getDriver() {
            return driver;
        }

        public String getLanguage() {
            return language;
        }

        public @Nullable String getPassword() {
            return password;
        }

        public String getUrl() {
            return url;
        }

        public @Nullable String getUsername() {
            return username;
        }

        public void setDriver(String driver) {
            this.driver = driver;
        }

        public void setLanguage(String language) {
            this.language = language;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public void setUsername(String username) {
            this.username = username;
        }
    }

    @NullUnmarked
    public static class DcatCatalog {
        @NotNull private URI uri = URI.create("http://localhost/ontopus/catalog");
        /** Description of the catalog */
        @NotEmpty private String description = "Catalog of published ontologies on this OntoPuS instance";
        /** Title of the catalog */
        @NotEmpty private String title = "OntoPuS Ontology Catalog";
        /** Language of the catalog metadata (title, description). */
        @Nullable private String language = null;

        /**
         * The type of catalog publisher. The publisher is an <a
         * href="http://xmlns.com/foaf/0.1/Agent">{@code foaf:Agent}</a>.
         *
         * <p>Example values:<br>
         * {@code http://xmlns.com/foaf/0.1/Person}<br>
         * {@code http://xmlns.com/foaf/0.1/Organization}
         */
        @NotNull private URI publisherType;

        /** The name of the catalog publisher */
        @NotEmpty private String publisherName;

        public String getDescription() {
            return description;
        }

        public @Nullable String getLanguage() {
            return language;
        }

        public String getPublisherName() {
            return publisherName;
        }

        public URI getPublisherType() {
            return publisherType;
        }

        public String getTitle() {
            return title;
        }

        public OntopusCatalogURI getUri() {
            return new OntopusCatalogURI(uri);
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public void setLanguage(@Nullable String language) {
            this.language = language;
        }

        public DcatCatalog setPublisherName(String publisherName) {
            this.publisherName = publisherName;
            return this;
        }

        public DcatCatalog setPublisherType(URI publisherType) {
            this.publisherType = publisherType;
            return this;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public void setUri(URI uri) {
            this.uri = uri;
        }
    }

    public static class Files {
        /** Directory for storing files used with ontology importing. */
        private Path importFilesDirectory = Path.of("./");

        private String defaultGlobPattern = "**.{nt,rdf,ttl,trig,trigs,brf,ttls}";

        public String getDefaultGlobPattern() {
            return defaultGlobPattern;
        }

        public Path getImportFilesDirectory() {
            return importFilesDirectory;
        }

        public void setDefaultGlobPattern(String defaultGlobPattern) {
            this.defaultGlobPattern = defaultGlobPattern;
        }

        public void setImportFilesDirectory(Path importFilesDirectory) {
            this.importFilesDirectory = importFilesDirectory;
        }
    }

    public static class Resource {
        /**
         * Request to {@code https} prefixed resource that does not exist, will fall back to the same resource with
         * {@code http}.
         */
        private boolean httpsFallsBackToHttp = true;
        /**
         * Request to {@code http} prefixed resource that does not exist, will fall back to the same resource with
         * {@code https}.
         */
        private boolean httpFallsBackToHttps = false;
        /**
         * Request to resource with trailing slash that does not exist, will fall back to the same resource without
         * trailing slash.
         */
        private boolean trailingSlashFallsBackToNoSlash = true;
        /**
         * Request to resource without trailing slash that does not exist, will fall back to the same resource with
         * trailing slash.
         */
        private boolean noSlashFallsBackToTrailingSlash = true;

        /** The value of {@code max-age} in cache control HTTP header */
        private Duration cacheControlMaxAge = Duration.ofDays(1);

        public Duration getCacheControlMaxAge() {
            return cacheControlMaxAge;
        }

        public boolean isHttpFallsBackToHttps() {
            return httpFallsBackToHttps;
        }

        public boolean isHttpsFallsBackToHttp() {
            return httpsFallsBackToHttp;
        }

        public boolean isNoSlashFallsBackToTrailingSlash() {
            return noSlashFallsBackToTrailingSlash;
        }

        public boolean isTrailingSlashFallsBackToNoSlash() {
            return trailingSlashFallsBackToNoSlash;
        }

        public Resource setCacheControlMaxAge(Duration cacheControlMaxAge) {
            this.cacheControlMaxAge = cacheControlMaxAge;
            return this;
        }

        public void setHttpFallsBackToHttps(boolean httpFallsBackToHttps) {
            this.httpFallsBackToHttps = httpFallsBackToHttps;
        }

        public void setHttpsFallsBackToHttp(boolean httpsFallsBackToHttp) {
            this.httpsFallsBackToHttp = httpsFallsBackToHttp;
        }

        public void setNoSlashFallsBackToTrailingSlash(boolean noSlashFallsBackToTrailingSlash) {
            this.noSlashFallsBackToTrailingSlash = noSlashFallsBackToTrailingSlash;
        }

        public void setTrailingSlashFallsBackToNoSlash(boolean trailingSlashFallsBackToNoSlash) {
            this.trailingSlashFallsBackToNoSlash = trailingSlashFallsBackToNoSlash;
        }
    }
}
