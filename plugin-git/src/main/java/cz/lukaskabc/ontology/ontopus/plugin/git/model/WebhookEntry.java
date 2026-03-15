package cz.lukaskabc.ontology.ontopus.plugin.git.model;

import cz.cvut.kbss.jopa.model.annotations.OWLClass;
import cz.lukaskabc.ontology.ontopus.core_model.generated.Vocabulary;
import cz.lukaskabc.ontology.ontopus.core_model.model.PersistenceEntity;
import cz.lukaskabc.ontology.ontopus.core_model.model.id.AbstractTypedIdentifier;
import cz.lukaskabc.ontology.ontopus.core_model.model.id.TypedIdentifier;
import tools.jackson.databind.JsonNode;

import java.net.URI;
import java.util.Set;

@OWLClass(iri = Vocabulary.s_c_WebhookEntry)
public class WebhookEntry extends PersistenceEntity<TypedIdentifier> {
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

    public boolean matches(JsonNode node) {
        return conditions.stream().allMatch(condition -> condition.test(node));
    }

    public void setConditions(Set<JsonPointerCondition> conditions) {
        this.conditions = conditions;
    }

    public void setMappings(Set<JsonMappingEntry> mappings) {
        this.mappings = mappings;
    }

    @Override
    protected TypedIdentifier wrapUri(URI uri) {
        return new AbstractTypedIdentifier(uri) {
            @Override
            public boolean equals(Object o) {
                return super.equals(o);
            }
        };
    }
}
