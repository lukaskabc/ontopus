package cz.lukaskabc.ontology.ontopus.core.rest.controller;

import cz.lukaskabc.ontology.ontopus.core.rest.mapper.DtoMapper;
import cz.lukaskabc.ontology.ontopus.core.rest.response.VersionSeriesListEntry;
import cz.lukaskabc.ontology.ontopus.core.rest.response.VersionSeriesResponse;
import cz.lukaskabc.ontology.ontopus.core_model.model.id.VersionSeriesURI;
import cz.lukaskabc.ontology.ontopus.core_model.model.ontology.VersionSeries;
import cz.lukaskabc.ontology.ontopus.core_model.service.VersionSeriesService;
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
public class VersionSeriesController {
    private final VersionSeriesService versionSeriesService;
    private final DtoMapper dtoMapper;

    public VersionSeriesController(VersionSeriesService versionSeriesService, DtoMapper dtoMapper) {
        this.versionSeriesService = versionSeriesService;
        this.dtoMapper = dtoMapper;
    }

    public VersionSeriesResponse getVersionSeries(String identifier) {
        final VersionSeriesURI uri = new VersionSeriesURI(identifier);
        final VersionSeries series = versionSeriesService.findRequiredById(uri);
        return dtoMapper.versionSeriesToResponse(series);
    }

    @Operation(
            summary = "Get version series or list",
            description = "Fetches a specific Version Series if the 'series' parameter is provided. "
                    + "If 'series' is omitted, it returns a paginated list of series.")
    @ApiResponse(
            responseCode = "200",
            description = "Successful response",
            content =
                    @Content(
                            mediaType = "application/json",
                            schema = @Schema(anyOf = {VersionSeriesResponse.class, VersionSeriesPage.class})))
    @GetMapping("/series")
    public ResponseEntity<?> versionSeriesList(
            @Parameter(description = "Optional URI identifier to fetch a single version series")
                    @RequestParam(name = "series", required = false)
                    String seriesIdentifier,
            @Parameter(description = "Pagination parameters (e.g., page, size, sort)") Pageable pageable,
            @Parameter(description = "Optional list of filters to apply to the collection")
                    @RequestParam(value = "filter", required = false)
                    List<String> filter) {

        if (seriesIdentifier != null) {
            return ResponseEntity.ok(getVersionSeries(seriesIdentifier));
        }

        final Page<@NonNull VersionSeriesListEntry> page =
                versionSeriesService.find(pageable, filter).map(dtoMapper::versionSeriesToListEntry);
        return ResponseEntity.ok(page);
    }

    public interface VersionSeriesPage extends Page<VersionSeriesListEntry> {}
}
