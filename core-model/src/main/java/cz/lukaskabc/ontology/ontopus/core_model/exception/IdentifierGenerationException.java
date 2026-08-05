package cz.lukaskabc.ontology.ontopus.core_model.exception;

import cz.lukaskabc.ontology.ontopus.core_model.generated.Vocabulary;
import org.springframework.http.HttpStatus;

public class IdentifierGenerationException extends InternalException {
    public IdentifierGenerationException(String internalMessage, String entityName) {
        super(
                HttpStatus.INTERNAL_SERVER_ERROR,
                Vocabulary.u_i_ontopus_problem_id_generation,
                internalMessage,
                "ontopus.core.error.uriGeneration.title",
                null,
                "ontopus.core.error.uriGeneration.detail",
                new Object[] {entityName});
    }
}
