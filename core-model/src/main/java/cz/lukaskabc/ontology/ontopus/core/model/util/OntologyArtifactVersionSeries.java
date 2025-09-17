package cz.lukaskabc.ontology.ontopus.core.model.util;

import cz.cvut.kbss.jopa.model.annotations.OWLClass;
import cz.cvut.kbss.jopa.model.annotations.OWLDataProperty;
import cz.lukaskabc.ontology.ontopus.core.generated.Vocabulary;
import cz.lukaskabc.ontology.ontopus.core.model.dcat.DatasetSeries;

@OWLClass(iri = Vocabulary.s_c_OntologyVersionSeries)
public class OntologyArtifactVersionSeries extends DatasetSeries {

    @OWLDataProperty(iri = Vocabulary.s_p_serializedImportContext, simpleLiteral = true)
    private SerializableImportProcessContext serializableImportProcessContext;

    public SerializableImportProcessContext getSerializableImportProcessContext() {
        return serializableImportProcessContext;
    }

    public void setSerializableImportProcessContext(SerializableImportProcessContext serializableImportProcessContext) {
        this.serializableImportProcessContext = serializableImportProcessContext;
    }
}
