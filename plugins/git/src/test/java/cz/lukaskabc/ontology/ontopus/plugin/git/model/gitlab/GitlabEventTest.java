package cz.lukaskabc.ontology.ontopus.plugin.git.model.gitlab;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;

class GitlabEventTest {
    @Test
    void rejectsUnsupportedHeaders() {
        assertNull(GitlabEvent.fromHeader("Merge Request Hook"));
        assertNull(GitlabEvent.fromHeader(null));
    }

    @Test
    void resolvesPushHeaders() {
        assertEquals(GitlabEvent.PUSH, GitlabEvent.fromHeader("Push Hook"));
        assertEquals(GitlabEvent.TAG_PUSH, GitlabEvent.fromHeader("Tag Push Hook"));
    }
}
