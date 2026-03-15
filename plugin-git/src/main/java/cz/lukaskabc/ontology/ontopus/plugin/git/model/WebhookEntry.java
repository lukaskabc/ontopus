package cz.lukaskabc.ontology.ontopus.plugin.git.model;

import cz.cvut.kbss.jopa.model.annotations.CascadeType;
import cz.cvut.kbss.jopa.model.annotations.OWLClass;
import cz.cvut.kbss.jopa.model.annotations.OWLObjectProperty;
import cz.lukaskabc.ontology.ontopus.core_model.generated.Vocabulary;
import cz.lukaskabc.ontology.ontopus.core_model.model.AbstractGeneratedPersistenceEntity;
import cz.lukaskabc.ontology.ontopus.core_model.model.id.VersionSeriesURI;
import cz.lukaskabc.ontology.ontopus.plugin.git.persistence.identifier.WebhookEntryURI;
import tools.jackson.databind.JsonNode;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.net.URI;
import java.util.Set;

@OWLClass(iri = Vocabulary.s_c_WebhookEntry)
public class WebhookEntry extends AbstractGeneratedPersistenceEntity<WebhookEntryURI> {
    @NotNull @OWLObjectProperty(iri = Vocabulary.s_p_dcat_subject)
    private URI versionSeries;

    @OWLObjectProperty(iri = Vocabulary.s_p_hasCondition, cascade = CascadeType.ALL)
    private Set<JsonPointerCondition> conditions;

    @NotEmpty @OWLObjectProperty(iri = Vocabulary.s_p_hasMapping, cascade = CascadeType.ALL)
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

    public VersionSeriesURI getVersionSeries() {
        return new VersionSeriesURI(versionSeries);
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

    public void setVersionSeries(VersionSeriesURI versionSeries) {
        this.versionSeries = versionSeries.toURI();
    }

    @Override
    protected WebhookEntryURI wrapUri(URI uri) {
        return new WebhookEntryURI(uri);
    }
}
