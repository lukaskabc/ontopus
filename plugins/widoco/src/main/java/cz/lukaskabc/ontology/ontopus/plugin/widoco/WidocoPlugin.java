package cz.lukaskabc.ontology.ontopus.plugin.widoco;

import cz.lukaskabc.ontology.ontopus.api.Plugin;
import cz.lukaskabc.ontology.ontopus.core_model.exception.InitializationException;

public class WidocoPlugin implements Plugin {
    public WidocoPlugin() {
        try {
            Class.forName("cz.lukaskabc.ontology.ontopus.plugin.rdf.RDFPlugin");
        } catch (ClassNotFoundException e) {
            throw new InitializationException("WidocoPlugin requires RDFPlugin to be present on the classpath", e);
        }
    }
}
