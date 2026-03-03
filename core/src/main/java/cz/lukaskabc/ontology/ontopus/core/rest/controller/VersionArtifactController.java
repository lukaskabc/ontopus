package cz.lukaskabc.ontology.ontopus.core.rest.controller;

import cz.lukaskabc.ontology.ontopus.core.rest.mapper.DtoMapper;
import cz.lukaskabc.ontology.ontopus.core.rest.response.VersionArtifactListEntry;
import cz.lukaskabc.ontology.ontopus.core.rest.response.VersionArtifactResponse;
import cz.lukaskabc.ontology.ontopus.core_model.model.id.VersionArtifactURI;
import cz.lukaskabc.ontology.ontopus.core_model.model.ontology.VersionArtifact;
import cz.lukaskabc.ontology.ontopus.core_model.service.VersionArtifactService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

    @GetMapping("/artifact")
    public VersionArtifactResponse getVersionArtifact(@RequestParam("identifier") String identifier) {
        final VersionArtifactURI uri = new VersionArtifactURI(identifier);
        final VersionArtifact artifact = versionArtifactService.findRequiredById(uri);
        return dtoMapper.versionArtifactToResponse(artifact);
    }

    @GetMapping("/artifacts")
    public Page<VersionArtifactListEntry> versionArtifactList(
            Pageable pageable, @RequestParam(value = "filter", required = false) List<String> filter) {
        return versionArtifactService.find(pageable, filter).map(dtoMapper::versionArtifactToListEntry);
    }
}
