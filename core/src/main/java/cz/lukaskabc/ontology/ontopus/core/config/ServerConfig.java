package cz.lukaskabc.ontology.ontopus.core.config;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "ontopus")
public class ServerConfig {
    @Valid private Database database;

    public Database getDatabase() {
        return database;
    }

    public void setDatabase(Database database) {
        this.database = database;
    }

    public static class Database {
        @NotNull private String driver;

        @NotNull private String language;

        private String password;

        @NotNull private String url;

        private String username;

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
}
