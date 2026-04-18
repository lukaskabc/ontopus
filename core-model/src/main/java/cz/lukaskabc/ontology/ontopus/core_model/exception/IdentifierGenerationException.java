package cz.lukaskabc.ontology.ontopus.core_model.exception;

import cz.lukaskabc.ontology.ontopus.core_model.generated.Vocabulary;
import org.springframework.http.HttpStatus;

public class IdentifierGenerationException extends InternalException {
    public IdentifierGenerationException(String internalMessage) {
        super(
                HttpStatus.INTERNAL_SERVER_ERROR,
                Vocabulary.u_i_id_generation,
                internalMessage,
                null,
                null,
                null,
                EMPTY_ARGUMENTS);
    }
}
