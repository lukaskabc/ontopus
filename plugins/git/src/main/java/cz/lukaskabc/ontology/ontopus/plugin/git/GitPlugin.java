package cz.lukaskabc.ontology.ontopus.plugin.git;

import cz.lukaskabc.ontology.ontopus.api.Plugin;
import cz.lukaskabc.ontology.ontopus.plugin.git.model.GithubWebhook;
import org.jspecify.annotations.NullMarked;

import java.util.List;

@NullMarked
public class GitPlugin implements Plugin {
    public static final String RESOURCE_PATH = "cz/lukaskabc/ontology/ontopus/plugin/git";
    public static final String FORM_RESOURCE_PATH = RESOURCE_PATH + "/form";

    @Override
    public List<String> getJopaScanPackages() {
        return List.of(GithubWebhook.class.getPackageName());
    }
}
