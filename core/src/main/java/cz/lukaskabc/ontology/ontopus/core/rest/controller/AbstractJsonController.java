package cz.lukaskabc.ontology.ontopus.core.rest.controller;

import cz.lukaskabc.ontology.ontopus.api.model.FormJsonDataDto;
import cz.lukaskabc.ontology.ontopus.core.exception.RestInvalidDataException;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.node.ArrayNode;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Map;

public class AbstractJsonController {
    private final ObjectMapper objectMapper;

    public AbstractJsonController(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    /**
     * Iterates over the parameters of the multipart request and parses them as JSON values.
     *
     * @param request the multipart request containing the parameters to be parsed
     * @return a map of parameter names to their parsed JSON values or strings
     */
    protected FormJsonDataDto parseData(HttpServletRequest request) {
        FormJsonDataDto result = new FormJsonDataDto(request.getParameterMap().size());
        for (Map.Entry<String, String[]> entry : request.getParameterMap().entrySet()) {
            result.put(entry.getKey(), parseDataValue(entry.getValue()));
        }
        return result;
    }

    /**
     * Reads the provided string values as JSON and returns the resulting JSON node. If there are multiple values, they
     * are returned as an array node.
     *
     * @param values the string values to be read as JSON
     * @return the JSON node from the single value or array node containing multiple parsed values
     */
    private JsonNode parseDataValue(String[] values) {
        try {
            if (values.length > 1) {
                ArrayNode node = objectMapper.createArrayNode();
                for (String value : values) {
                    node.add(objectMapper.readTree(value));
                }
                return node;
            } else {
                return objectMapper.readTree(values[0]);
            }
        } catch (JacksonException e) {
            throw new RestInvalidDataException("Failed to parse string value as JSON", e);
        }
    }
}
