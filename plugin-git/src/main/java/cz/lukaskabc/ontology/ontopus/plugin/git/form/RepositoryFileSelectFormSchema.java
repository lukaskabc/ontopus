package cz.lukaskabc.ontology.ontopus.plugin.git.form;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import cz.lukaskabc.ontology.ontopus.api.util.JsonResourceLoader;
import cz.lukaskabc.ontology.ontopus.plugin.git.GitPlugin;
import java.util.List;
import lombok.Getter;

@Getter
public class RepositoryFileSelectFormSchema {
    public static JsonNode loadUiSchema() {
        return JsonResourceLoader.loadUiSchema(GitPlugin.FORM_RESOURCE_PATH, "RepositoryFileSelectForm");
    }

    private final String type = "array";
    private final boolean uniqueItems = true;

    private final ArrayItems items;

    public RepositoryFileSelectFormSchema(List<String> files) {
        this.items = new ArrayItems(files);
    }

    @Getter
    public static class ArrayItems {
        private final String type = "string";

        @JsonProperty("enum")
        private final List<String> enumList;

        public ArrayItems(List<String> enumList) {
            this.enumList = enumList;
        }
    }
}
