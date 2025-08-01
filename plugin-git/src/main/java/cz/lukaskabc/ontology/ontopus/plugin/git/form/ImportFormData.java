package cz.lukaskabc.ontology.ontopus.plugin.git.form;

import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;
import org.jspecify.annotations.Nullable;

@Getter
@Setter
public class ImportFormData {
    @NotEmpty private String repositoryUrl;

    @Nullable private String branch;

    @Nullable private String username;

    @Nullable private String password;
}
