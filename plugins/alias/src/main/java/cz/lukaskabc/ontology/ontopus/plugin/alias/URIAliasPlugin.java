package cz.lukaskabc.ontology.ontopus.plugin.alias;

import cz.lukaskabc.ontology.ontopus.api.Plugin;

import java.util.List;

/** Allows specifying global URI to URI alias mappings */
public class URIAliasPlugin implements Plugin {
    public static final String FORMS_PATH = "form";
    /**
     * List of packages that should be scanned by JOPA for entity declarations.
     *
     * @return list of packages to scan for JOPA entities.
     */
    @Override
    public List<String> getJopaScanPackages() {
        return getSpringScanPackages();
    }
}
