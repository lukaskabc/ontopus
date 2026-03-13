package cz.lukaskabc.ontology.ontopus.plugin.git.webhook.entry;

import org.jspecify.annotations.Nullable;
import tools.jackson.core.JsonPointer;
import tools.jackson.databind.JsonNode;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/** Object holding a JSON pointer and regex pattern for matching the value specified by the pointer. */
public class JsonPointerCondition {
    private JsonPointer jsonPointer;
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
