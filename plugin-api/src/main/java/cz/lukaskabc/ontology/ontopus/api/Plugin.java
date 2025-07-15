package cz.lukaskabc.ontology.ontopus.api;

public interface Plugin<Self extends Plugin<Self>> {
    String getName();
    Self initialize();
}
