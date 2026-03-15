package cz.lukaskabc.ontology.ontopus.plugin.git.model;

import cz.cvut.kbss.jopa.model.annotations.OWLClass;
import cz.cvut.kbss.jopa.model.annotations.OWLDataProperty;
import cz.lukaskabc.ontology.ontopus.core_model.generated.Vocabulary;
import org.jspecify.annotations.Nullable;
import tools.jackson.core.JsonPointer;
import tools.jackson.databind.JsonNode;

import jakarta.validation.constraints.NotNull;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/** Object holding a JSON pointer and regex pattern for matching the value specified by the pointer. */
@OWLClass(iri = Vocabulary.s_c_JsonPointerCondition)
public class JsonPointerCondition {
    @NotNull @OWLDataProperty(iri = Vocabulary.s_p_sourceJsonPointer)
    private JsonPointer jsonPointer;

    @NotNull @OWLDataProperty(iri = Vocabulary.s_p_regexPattern)
    private Pattern regex;

    public JsonPointer getJsonPointer() {
        return jsonPointer;
    }

    public Pattern getRegex() {
        return regex;
    }

    @Nullable public Matcher matcher(JsonNode jsonNode) {
        if (!jsonNode.isValueNode()) {
            return null;
        }
        return this.regex.matcher(jsonNode.toString());
    }

    public void setJsonPointer(JsonPointer jsonPointer) {
        this.jsonPointer = jsonPointer;
    }

    public void setRegex(Pattern regex) {
        this.regex = regex;
    }

    /**
     * Resolves a value node specified by the {@link #jsonPointer} and tests it against the regex pattern. If the
     * pointer does not resolve to a value node, returns false.
     *
     * @param jsonNode the JSON node to test
     * @return true when the pointer resolved to value node which was <b>partially</b> matched against the pattern,
     *     false otherwise.
     * @see Matcher#find()
     */
    public boolean test(JsonNode jsonNode) {
        final JsonNode target = jsonNode.at(this.jsonPointer);
        final Matcher matcher = this.matcher(target);
        return matcher != null && matcher.find();
    }

    /**
     * Resolves a value node specified by the {@link #jsonPointer} and tests it against the regex pattern. If the
     * pointer does not resolve to a value node, returns false.
     *
     * @param jsonNode the JSON node to test
     * @return true when the pointer resolved to value node which was <b>fully</b> matched against the pattern, false
     *     otherwise.
     * @see Matcher#matches()
     */
    public boolean testFull(JsonNode jsonNode) {
        final JsonNode target = jsonNode.at(this.jsonPointer);
        final Matcher matcher = this.matcher(target);
        return matcher != null && matcher.matches();
    }
}
