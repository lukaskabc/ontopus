package cz.lukaskabc.ontology.ontopus.core.config;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Getter
@Setter
@Validated
@ConfigurationProperties(prefix = "ontopus")
public class ServerConfig {
    @Valid private Database database;

    @Getter
    @Setter
    public static class Database {
        @NotNull private String driver;

        @NotNull private String language;

        private String password;

        @NotNull private String url;

        private String username;
    }
}
