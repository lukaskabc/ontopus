package cz.lukaskabc.ontology.ontopus.plugin.git.model.gitlab;

import org.jspecify.annotations.Nullable;

public enum GitlabEvent {
    PUSH("Push Hook"),
    TAG_PUSH("Tag Push Hook");

    public static @Nullable GitlabEvent fromHeader(@Nullable String value) {
        if (value == null) return null;
        for (GitlabEvent event : values()) {
            if (event.headerValue.equalsIgnoreCase(value)) return event;
        }
        return null;
    }

    private final String headerValue;

    GitlabEvent(String headerValue) {
        this.headerValue = headerValue;
    }

    public String getHeaderValue() {
        return headerValue;
    }
}
