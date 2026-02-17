package cz.lukaskabc.ontology.ontopus.core.service.process;

import cz.lukaskabc.ontology.ontopus.api.model.FormResult;
import cz.lukaskabc.ontology.ontopus.api.model.ImportProcessContext;
import cz.lukaskabc.ontology.ontopus.api.model.JsonForm;
import cz.lukaskabc.ontology.ontopus.api.service.import_process.ImportProcessingService;
import cz.lukaskabc.ontology.ontopus.core_model.exception.OntopusException;
import org.jspecify.annotations.Nullable;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.node.ObjectNode;
import tools.jackson.databind.node.StringNode;

import java.net.URI;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class OntologyIdentifierSelector implements ImportProcessingService<URI> {
    private static final String ID_FIELD = "identifier";
    private final JsonForm jsonForm;
    private final ObjectMapper objectMapper;

    public OntologyIdentifierSelector(ObjectMapper objectMapper, Set<URI> identifiers) {
        this.objectMapper = objectMapper;
        this.jsonForm = makeJsonForm(identifiers);
    }

    @Override
    public @Nullable JsonForm getJsonForm() {
        return jsonForm;
    }

    @Override
    public String getServiceName() {
        return "";
    }

    @Override
    public URI handleSubmit(FormResult formResult, ImportProcessContext context) {
        try {
            String identifier = formResult.getStringValue(ID_FIELD);
            Objects.requireNonNull(identifier);
            return URI.create(identifier);
        } catch (Exception e) {
            throw new OntopusException(e); // TODO exception
        }
    }

    protected JsonForm makeJsonForm(Set<URI> identifiers) {
        List<String> stringIds =
                identifiers.stream().map(URI::toString).sorted().toList();
        Set<StringNode> examples = stringIds.stream().map(StringNode::valueOf).collect(Collectors.toSet());

        ObjectNode scheme = objectMapper.createObjectNode();
        scheme.put("type", "object");
        ObjectNode properties = scheme.putObject("properties");
        properties
                .putObject(ID_FIELD)
                .put("type", "string")
                // not limiting the format to URI
                // since the frontend URI validation may be too strict
                .put(
                        "title",
                        "ontopus.core.service.ImportProcessingService.OntologyIdentifierSelector.field.identifier.title")
                .putArray("examples")
                .addAll(examples);

        ObjectNode formData = objectMapper.createObjectNode();
        if (!stringIds.isEmpty()) {
            formData.put(ID_FIELD, stringIds.getFirst());
        }

        return new JsonForm(scheme, null, formData);
    }
}
