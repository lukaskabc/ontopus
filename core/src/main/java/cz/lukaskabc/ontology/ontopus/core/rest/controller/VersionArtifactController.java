package cz.lukaskabc.ontology.ontopus.core.rest.controller;

import cz.lukaskabc.ontology.ontopus.core.rest.mapper.DtoMapper;
import cz.lukaskabc.ontology.ontopus.core.rest.response.VersionArtifactListEntry;
import cz.lukaskabc.ontology.ontopus.core.rest.response.VersionArtifactResponse;
import cz.lukaskabc.ontology.ontopus.core.rest.response.VersionSeriesResponse;
import cz.lukaskabc.ontology.ontopus.core_model.model.id.VersionArtifactURI;
import cz.lukaskabc.ontology.ontopus.core_model.model.id.VersionSeriesURI;
import cz.lukaskabc.ontology.ontopus.core_model.model.ontology.VersionArtifact;
import cz.lukaskabc.ontology.ontopus.core_model.service.VersionArtifactService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.jspecify.annotations.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class VersionArtifactController {
    private final VersionArtifactService versionArtifactService;
    private final DtoMapper dtoMapper;

    public VersionArtifactController(VersionArtifactService versionArtifactService, DtoMapper dtoMapper) {
        this.versionArtifactService = versionArtifactService;
        this.dtoMapper = dtoMapper;
    }

    public VersionArtifactResponse getVersionArtifact(String identifier) {
        final VersionArtifactURI uri = new VersionArtifactURI(identifier);
        final VersionArtifact artifact = versionArtifactService.findRequiredById(uri);
        return dtoMapper.versionArtifactToResponse(artifact);
    }

    @Operation(
            summary = "Get version artifact or list",
            description = "Fetches a specific Version Artifact if the 'artifact' parameter is provided. "
                    + "If 'artifact' is omitted, it returns a paginated list of artifacts.")
    @ApiResponse(
            responseCode = "200",
            description = "Successful response",
            content =
                    @Content(
                            mediaType = "application/json",
                            schema = @Schema(anyOf = {VersionSeriesResponse.class, VersionArtifactPage.class})))
    @GetMapping("/series/artifact")
    public ResponseEntity<?> versionArtifactList(
            @Parameter(description = "Optional URI identifier to fetch a single version artifact")
                    @RequestParam(name = "artifact", required = false)
                    String artifactIdentifier,
            @Parameter(description = "URI identifier of the version series to fetch artifacts for")
                    @RequestParam(name = "series", required = false)
                    String seriesIdentifier,
            @Parameter(description = "Pagination parameters (e.g., page, size, sort)") Pageable pageable,
            @Parameter(description = "Optional list of filters to apply to the collection")
                    @RequestParam(value = "filter", required = false)
                    List<String> filter) {

        if (artifactIdentifier != null) {
            return ResponseEntity.ok(getVersionArtifact(artifactIdentifier));
        }

        if (seriesIdentifier == null) {
            return ResponseEntity.badRequest()
                    .body("Missing required 'series' parameter when 'artifact' is not provided.");
        }

        final VersionSeriesURI seriesURI = new VersionSeriesURI(seriesIdentifier);

        final Page<@NonNull VersionArtifactListEntry> page =
                versionArtifactService.find(seriesURI, pageable, filter).map(dtoMapper::versionArtifactToListEntry);
        return ResponseEntity.ok(page);
    }

    public interface VersionArtifactPage extends Page<VersionArtifactListEntry> {}
}
