package cz.lukaskabc.ontology.ontopus.core_model.exception;

import org.jspecify.annotations.Nullable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.net.URI;

@ResponseStatus(code = HttpStatus.NOT_FOUND, reason = "Resource not found")
public class NotFoundException extends OntopusException {
    public static final URI TYPE = URI.create(TYPE_NAMESPACE + "not-found");

    public NotFoundException(String detailMessageCode) {
        this(detailMessageCode, null);
    }

    public NotFoundException(String internalMessage, @Nullable Throwable cause) {
        super(HttpStatus.FORBIDDEN, TYPE, internalMessage, cause);
    }
}
