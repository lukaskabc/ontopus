package cz.lukaskabc.ontology.ontopus.core_model.exception;

import org.jspecify.annotations.Nullable;
import org.springframework.http.HttpStatus;

import java.net.URI;

public class OntopusSecurityException extends OntopusException {
    public static final URI TYPE = URI.create(TYPE_NAMESPACE + "access-denied");

    public OntopusSecurityException(String detailMessageCode) {
        this(detailMessageCode, null);
    }

    public OntopusSecurityException(String internalMessage, @Nullable Throwable cause) {
        super(HttpStatus.FORBIDDEN, TYPE, internalMessage, cause);
    }
}
