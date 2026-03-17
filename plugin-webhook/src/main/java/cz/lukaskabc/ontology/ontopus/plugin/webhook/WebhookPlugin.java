package cz.lukaskabc.ontology.ontopus.plugin.webhook;

import cz.lukaskabc.ontology.ontopus.api.Plugin;
import cz.lukaskabc.ontology.ontopus.plugin.webhook.model.WebhookEntry;
import org.jspecify.annotations.NullMarked;

import java.util.List;

@NullMarked
public class WebhookPlugin implements Plugin {
    public static final String RESOURCE_PATH = "cz/lukaskabc/ontology/ontopus/plugin/git";
    public static final String FORM_RESOURCE_PATH = RESOURCE_PATH + "/form";

    @Override
    public List<String> getJopaScanPackages() {
        return List.of(WebhookEntry.class.getPackageName());
    }
}
