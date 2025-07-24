package cz.lukaskabc.ontology.ontopus.core.rest;

import cz.lukaskabc.ontology.ontopus.api.Plugin;
import cz.lukaskabc.ontology.ontopus.core.model.Ontology;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.util.List;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.treewalk.TreeWalk;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/")
class Controller {
    @Autowired(required = false)
    private List<Plugin> plugins = List.of();

    @GetMapping(produces = "application/rdf+xml")
    public ResponseEntity<String> getOntology() {
        final var v = new Ontology()
                .setIri(URI.create("http://ontology.lukaskabc.cz/Ontology_test#instance123456"))
                .setVersionInfo("Version Info text")
                .setVersionIri(URI.create("http://ontology.lukaskabc.cz/Ontology_test#instance123456/1.0/"));
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType("application/rdf+xml"))
                .body(v.toString());
    }

    @PostMapping("/git")
    public void importGitRepository(@RequestBody URI repositoryUri) throws GitAPIException, IOException {
        final var dir = Files.createTempDirectory("ontopus-gitrepository").toFile();
        dir.deleteOnExit();
        final var cmd = Git.cloneRepository()
                .setURI(repositoryUri.toString())
                .setDirectory(dir)
                .setDepth(1)
                .setBranch("main")
                .setNoTags();
        try (final var git = cmd.call()) {
            final var walk = new TreeWalk(git.getRepository());
        }

        System.out.println("Git repository: " + repositoryUri.toString());
    }

    // @GetMapping("plugins")
    // public ResponseEntity<?> getPlugins() {
    // if (plugins.isEmpty()) {
    // return ResponseEntity.noContent().build();
    // }
    // return ResponseEntity.ok(plugins.stream()
    // .map(Plugin::getName)
    // .toList());
    // }

    // @GetMapping("core")
    // public ResponseEntity<?> get() {
    // return ResponseEntity.ok("Core module is running");
    // }
}
