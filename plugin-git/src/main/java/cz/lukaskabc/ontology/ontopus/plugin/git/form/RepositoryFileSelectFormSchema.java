package cz.lukaskabc.ontology.ontopus.plugin.git.form;

import com.fasterxml.jackson.annotation.JsonProperty;
import cz.lukaskabc.ontology.ontopus.api.util.JsonResourceLoader;
import cz.lukaskabc.ontology.ontopus.plugin.git.GitPlugin;
import tools.jackson.databind.JsonNode;

import java.util.List;

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

    public ArrayItems getItems() {
        return items;
    }

    public String getType() {
        return type;
    }

    public boolean isUniqueItems() {
        return uniqueItems;
    }

    public static class ArrayItems {
        private final String type = "string";

        @JsonProperty("enum")
        private final List<String> enumList;

        public ArrayItems(List<String> enumList) {
            this.enumList = enumList;
        }

        public List<String> getEnumList() {
            return enumList;
        }

        public String getType() {
            return type;
        }
    }
}
