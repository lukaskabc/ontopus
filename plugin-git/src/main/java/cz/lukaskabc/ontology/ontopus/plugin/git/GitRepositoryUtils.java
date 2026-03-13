package cz.lukaskabc.ontology.ontopus.plugin.git;

import cz.lukaskabc.ontology.ontopus.core_model.util.StringUtils;
import cz.lukaskabc.ontology.ontopus.plugin.git.exception.GitException;
import cz.lukaskabc.ontology.ontopus.plugin.git.import_process.GitRepositoryClonningRequest;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.jgit.api.CloneCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
import org.springframework.util.FileSystemUtils;

import java.io.File;

public class GitRepositoryUtils {

    private static final Logger log = LogManager.getLogger(GitRepositoryUtils.class);

    /**
     * Clones the Git repository specified by the user in the import form. The repository is cloned with a depth of 1
     * (without history).
     *
     * @param formData the submitted to the form
     */
    public static void cloneRepository(GitRepositoryClonningRequest formData, File targetDirectory) {
        final CloneCommand cmd =
                Git.cloneRepository().setDirectory(targetDirectory).setDepth(1).setURI(formData.repositoryUrl());

        if (StringUtils.hasText(formData.branch())) {
            cmd.setBranch(formData.branch());
        }

        if (StringUtils.hasText(formData.username()) && StringUtils.hasText(formData.password())) {
            cmd.setCredentialsProvider(
                    new UsernamePasswordCredentialsProvider(formData.username(), formData.password()));
        }

        log.debug("Cloning repository from {} to {}", formData.repositoryUrl(), targetDirectory.getAbsolutePath());

        try {
            // Execute the clone and close the repository, the objects are not needed
            cmd.call().close();
        } catch (GitAPIException e) {
            log.error(
                    "Failed to clone repository from {} to {}, deleting target directory...",
                    formData.repositoryUrl(),
                    targetDirectory.getAbsolutePath(),
                    e);
            FileSystemUtils.deleteRecursively(targetDirectory);
            throw new GitException("Failed to clone repository from " + formData.repositoryUrl(), e);
        }
    }

    private GitRepositoryUtils() {
        throw new AssertionError();
    }
}
