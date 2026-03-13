package cz.lukaskabc.ontology.ontopus.plugin.git.webhook.entry;

import tools.jackson.databind.JsonNode;

import java.util.Set;
import java.util.UUID;

public class WebhookEntry {
    private UUID uuid;
    private Set<JsonPointerCondition> conditions;
    private Set<JsonMappingEntry> mappings;

    public void applyMapping(JsonNode source, JsonNode target) {
        mappings.forEach(mapping -> mapping.apply(source, target));
    }

    public Set<JsonPointerCondition> getConditions() {
        return conditions;
    }

    public Set<JsonMappingEntry> getMappings() {
        return mappings;
    }

    public UUID getUuid() {
        return uuid;
    }

    public boolean matches(JsonNode node) {
        return conditions.stream().allMatch(condition -> condition.test(node));
    }

    public void setConditions(Set<JsonPointerCondition> conditions) {
        this.conditions = conditions;
    }

    public void setMappings(Set<JsonMappingEntry> mappings) {
        this.mappings = mappings;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }
}
