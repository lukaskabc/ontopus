package cz.lukaskabc.ontology.ontopus.core.util;

import cz.lukaskabc.ontology.ontopus.core_model.util.TimeProvider;
import org.springframework.stereotype.Component;

import java.time.Clock;

@Component
public class ClockProvider implements TimeProvider {
    private final Clock clock = Clock.systemDefaultZone();

    @Override
    public Clock getClock() {
        return clock;
    }
}
