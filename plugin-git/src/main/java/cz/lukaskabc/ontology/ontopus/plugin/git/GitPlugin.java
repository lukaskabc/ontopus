package cz.lukaskabc.ontology.ontopus.plugin.git;

import cz.lukaskabc.ontology.ontopus.api.Plugin;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class GitPlugin implements Plugin {

    @Override
    public String getName() {
        return "GitHub";
    }
}
