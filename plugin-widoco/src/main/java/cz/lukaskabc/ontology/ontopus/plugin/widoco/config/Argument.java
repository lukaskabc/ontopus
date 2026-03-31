package cz.lukaskabc.ontology.ontopus.plugin.widoco.config;

import java.util.Set;

public enum Argument {
    CONF_FILE("confFile", "string"),
    ONT_FILE("ontFile", "string"),
    UNITE_SECTIONS("uniteSections", "boolean"),
    ANALYTICS("analytics", "string"),
    CROSS_REF("crossRef", "boolean"),
    DISPLAY_DIRECT_IMPORTS_ONLY("displayDirectImportsOnly", "boolean"),
    DO_NOT_DISPLAY_SERIALIZATIONS("doNotDisplaySerializations", "boolean"),
    ;
    public static Set<Argument> FILES = Set.of(CONF_FILE, ONT_FILE);

    private final String argument;
    /** Type for JSON schema */
    private final String schemaType;

    Argument(String argument, String schemaType) {
        this.argument = argument;
        this.schemaType = schemaType;
    }

    public String argument() {
        return "-" + argument;
    }

    public String schemaType() {
        return schemaType;
    }

    /**
     * Variable expression using the name of the argument
     *
     * @return String: {@code ${name()}} e.g. {@code ${CONF_FILE}}
     */
    public String variable() {
        return "${" + name() + "}";
    }
}
