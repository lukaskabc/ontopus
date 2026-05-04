package cz.lukaskabc.ontology.ontopus.plugin.widoco;

import cz.lukaskabc.ontology.ontopus.plugin.widoco.config.Argument;

import java.util.Set;

public class WidocoConstants {
    public static final Set<String> WIDOCO_OUTPUT_DIRECTORIES = Set.of("provenance", "sections", "resources");

    public static final Set<Argument> WIDOCO_DISALLOWED_ARGS = Set.of(Argument.ONT_FILE);

    public static final String CONTEXT_ADDITIONAL_PROPERTY_LOG_FILE = "LastWidocoExecutionLogFile";

    private WidocoConstants() {
        throw new AssertionError();
    }
}
