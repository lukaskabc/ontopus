package cz.lukaskabc.ontology.ontopus.core_model.util;

import jakarta.validation.ClockProvider;
import java.time.Instant;
import java.time.LocalDateTime;

public interface TimeProvider extends ClockProvider {

    default LocalDateTime getCurrentDateTime() {
        return LocalDateTime.now(getClock());
    }

    default Instant getInstant() {
        return getClock().instant();
    }
}
