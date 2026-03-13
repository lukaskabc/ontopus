package cz.lukaskabc.ontology.ontopus.plugin.git.webhook.entry;

import org.apache.commons.lang3.RegExUtils;
import tools.jackson.core.JsonPointer;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.node.ArrayNode;
import tools.jackson.databind.node.ObjectNode;
import tools.jackson.databind.node.StringNode;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * JSON to JSON mapping entry, holding source and target JSON pointers and a regex pattern for matching the value
 * specified by the source pointer.
 */
public class JsonMappingEntry {
    private static final Pattern DEFAULT_VALUE_PATTERN = RegExUtils.dotAll(".*");

    public static void replaceNodeAtPointer(JsonNode root, JsonPointer pointer, JsonNode newValue) {
        JsonPointer parentPointer = pointer.head();
        JsonNode parentNode = root.at(parentPointer);

        if (parentNode.isMissingNode()) {
            throw new IllegalArgumentException("Invalid JsonPointer: The parent node does not exist.");
        }

        JsonPointer lastSegment = pointer.last();
        if (parentNode.isObject()) {
            String propertyName = lastSegment.getMatchingProperty();
            ((ObjectNode) parentNode).set(propertyName, newValue);
        } else if (parentNode.isArray()) {
            int index = lastSegment.getMatchingIndex();
            if (index >= 0 && index < parentNode.size()) {
                ((ArrayNode) parentNode).set(index, newValue);
            } else {
                throw new IndexOutOfBoundsException("Invalid array index in JsonPointer.");
            }
        } else {
            throw new IllegalArgumentException("Cannot replace node: Parent is not an ObjectNode or ArrayNode.");
        }
    }

    private JsonPointer sourcePointer;
    private JsonPointer targetPointer;

    private Pattern valuePattern = DEFAULT_VALUE_PATTERN;

    public void apply(JsonNode source, JsonNode target) {
        final JsonNode sourceValue = source.at(this.sourcePointer);
        final JsonNode targetValue = target.at(this.targetPointer);
        if (!sourceValue.isValueNode() || !targetValue.isValueNode()) {
            return;
        }
        final String value = sourceValue.toString();
        final Matcher matcher = this.valuePattern.matcher(value);

        if (matcher.find()) {
            final String result = matcher.group();
            final StringNode newValue = new StringNode(result);
            replaceNodeAtPointer(target, this.targetPointer, newValue);
        }
    }

    public JsonPointer getSourcePointer() {
        return sourcePointer;
    }

    public JsonPointer getTargetPointer() {
        return targetPointer;
    }

    public Pattern getValuePattern() {
        return valuePattern;
    }

    public void setSourcePointer(JsonPointer sourcePointer) {
        this.sourcePointer = sourcePointer;
    }

    public void setTargetPointer(JsonPointer targetPointer) {
        this.targetPointer = targetPointer;
    }

    public void setValuePattern(Pattern valuePattern) {
        this.valuePattern = valuePattern;
    }
}
