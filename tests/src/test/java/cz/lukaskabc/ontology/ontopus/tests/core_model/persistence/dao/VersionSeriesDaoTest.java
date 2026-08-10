package cz.lukaskabc.ontology.ontopus.tests.core_model.persistence.dao;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import cz.lukaskabc.ontology.ontopus.core_model.model.id.VersionSeriesURI;
import cz.lukaskabc.ontology.ontopus.core_model.model.ontology.VersionSeries;
import cz.lukaskabc.ontology.ontopus.core_model.model.ontology.VersionSeries_;
import cz.lukaskabc.ontology.ontopus.core_model.model.util.FormDataDto;
import cz.lukaskabc.ontology.ontopus.core_model.model.util.SerializableImportProcessContext;
import cz.lukaskabc.ontology.ontopus.tests.persistence.BaseDaoTest;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.UUID;

public class VersionSeriesDaoTest extends BaseDaoTest {
    @Test
    void savingVersionSeriesPutsSerializableContextIntoCorrectGraph() {
        final VersionSeries series = seriesWithSerializableContext();

        boolean exists = em.createNativeQuery("ASK { GRAPH ?graph { ?series ?hasContext ?context } }", Boolean.class)
                .setParameter("graph", VersionSeries_.serializableImportProcessContextPropertyIRI)
                .setParameter("series", series.getIdentifier().toURI())
                .setParameter("hasContext", VersionSeries_.serializableImportProcessContextPropertyIRI)
                .getSingleResult();

        assertTrue(exists);
    }
    /**
     * Ensures that {@link cz.cvut.kbss.jopa.model.annotations.Context @Context} is applied to the
     * {@link VersionSeries#serializableImportProcessContext} correctly and the value is put into different context
     */
    @Test
    void savingVersionSeriesPutsSerializedContextIntoDifferentGraph() {
        final VersionSeries series = seriesWithSerializableContext();

        boolean exists = em.createNativeQuery("ASK { GRAPH ?graph { ?series ?hasContext ?context } }", Boolean.class)
                .setParameter("graph", VersionSeries_.entityClassIRI)
                .setParameter("series", series.getIdentifier().toURI())
                .setParameter("hasContext", VersionSeries_.serializableImportProcessContextPropertyIRI)
                .getSingleResult();

        assertFalse(exists);
    }

    private VersionSeries seriesWithSerializableContext() {
        final UUID uuid = UUID.randomUUID();
        final VersionSeries series = new VersionSeries();
        series.setIdentifier(new VersionSeriesURI("http://example.com/ontology/series/" + uuid));
        final SerializableImportProcessContext serializableContext = new SerializableImportProcessContext();
        final FormDataDto dataDto = new FormDataDto();
        dataDto.put("exampleKey_" + uuid, "exampleValue_" + uuid);
        serializableContext.setServiceToFormResultMap(Map.of("testService_" + uuid, dataDto));
        series.setSerializableImportProcessContext(serializableContext);
        withEntities(series);
        return series;
    }
}
