package cz.lukaskabc.ontology.ontopus.plugin.git.form;

import jakarta.validation.constraints.NotEmpty;
import org.jspecify.annotations.Nullable;

public class ImportFormData {
    @NotEmpty private String repositoryUrl;

    @Nullable private String branch;

    @Nullable private String username;

    @Nullable private String password;

    public @Nullable String getBranch() {
        return branch;
    }

    public @Nullable String getPassword() {
        return password;
    }

    public String getRepositoryUrl() {
        return repositoryUrl;
    }

    public @Nullable String getUsername() {
        return username;
    }

    public void setBranch(@Nullable String branch) {
        this.branch = branch;
    }

    public void setPassword(@Nullable String password) {
        this.password = password;
    }

    public void setRepositoryUrl(String repositoryUrl) {
        this.repositoryUrl = repositoryUrl;
    }

    public void setUsername(@Nullable String username) {
        this.username = username;
    }
}
