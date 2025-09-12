package cz.lukaskabc.ontology.ontopus.core.config;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.net.URI;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "ontopus")
public class OntopusConfig {

    @Valid private Database database = new Database();

    @Valid private DcatCatalog dcatCatalog = new DcatCatalog();

    public Database getDatabase() {
        return database;
    }

    public DcatCatalog getDcatCatalog() {
        return dcatCatalog;
    }

    public void setDatabase(Database database) {
        this.database = database;
    }

    public void setDcatCatalog(DcatCatalog dcatCatalog) {
        this.dcatCatalog = dcatCatalog;
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
        private String username;

        /** Password for authentication with database repository */
        private String password;

        /** Database repository URL */
        @NotNull private String url = "http://localhost:7200/repositories/ontopus";

        public String getDriver() {
            return driver;
        }

        public String getLanguage() {
            return language;
        }

        public String getPassword() {
            return password;
        }

        public String getUrl() {
            return url;
        }

        public String getUsername() {
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

    public static class DcatCatalog {
        private URI uri = URI.create("http://localhost/ontopus/catalog");
        /** Description of the catalog */
        private String description = "Catalog of published ontologies on this OntoPuS instance";
        /** Title of the catalog */
        private String title = "OntoPuS Ontology Catalog";

        public String getDescription() {
            return description;
        }

        public String getTitle() {
            return title;
        }

        public URI getUri() {
            return uri;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public void setUri(URI uri) {
            this.uri = uri;
        }
    }
}
