import org.jspecify.annotations.NullMarked;

@NullMarked
module plugin.api {
    requires org.jspecify;
    exports cz.lukaskabc.ontology.ontopus.api;
}