package cz.lukaskabc.ontology.ontopus.core.rest;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.treewalk.TreeWalk;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/")
class Controller {

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
