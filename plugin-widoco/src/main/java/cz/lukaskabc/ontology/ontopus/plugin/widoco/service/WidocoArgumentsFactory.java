package cz.lukaskabc.ontology.ontopus.plugin.widoco.service;

import cz.lukaskabc.ontology.ontopus.api.model.ImportProcessContext;
import cz.lukaskabc.ontology.ontopus.api.util.FileUtils;
import cz.lukaskabc.ontology.ontopus.core_model.exception.JsonFormSubmitException;
import cz.lukaskabc.ontology.ontopus.core_model.exception.OntopusException;
import cz.lukaskabc.ontology.ontopus.core_model.generated.Vocabulary;
import cz.lukaskabc.ontology.ontopus.core_model.model.id.GraphURI;
import cz.lukaskabc.ontology.ontopus.core_model.model.util.FormResult;
import cz.lukaskabc.ontology.ontopus.core_model.service.GraphService;
import cz.lukaskabc.ontology.ontopus.core_model.util.StringUtils;
import cz.lukaskabc.ontology.ontopus.plugin.widoco.WidocoConstants;
import cz.lukaskabc.ontology.ontopus.plugin.widoco.config.Argument;
import cz.lukaskabc.ontology.ontopus.plugin.widoco.config.WidocoArguments;
import org.jspecify.annotations.Nullable;
import org.springframework.stereotype.Service;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.node.StringNode;

import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class WidocoArgumentsFactory {
    public static boolean documentAllLanguages(FormResult formResult) {
        return Optional.ofNullable(formResult.formData().get("allLangs"))
                .filter(JsonNode::isBoolean)
                .map(JsonNode::asBoolean)
                .orElse(false);
    }

    private final GraphService graphService;

    public WidocoArgumentsFactory(GraphService graphService) {
        this.graphService = graphService;
    }

    public WidocoArguments build(FormResult formResult, ImportProcessContext context) {
        final WidocoArgumentsBuilder builder = new WidocoArgumentsBuilder(context);
        if (documentAllLanguages(formResult)) {
            builder.documentAllLanguages();
        }

        formResult.formData().forEach(builder::add);
        return builder.build();
    }

    /**
     * Resolves all languages used by literals in the given graph. Languages containing "-" are excluded.
     *
     * @param graphURI the database graph
     * @return stream of language tags
     */
    public Stream<String> resolveAlLanguages(GraphURI graphURI) {
        final List<String> languages = graphService.findAllLanguageTags(graphURI);
        if (!languages.isEmpty()) {
            return languages.stream().filter(lang -> !lang.contains("-"));
        }
        return Stream.empty();
    }

    protected class WidocoArgumentsBuilder {
        @Nullable private static Argument parseArgument(String name) {
            try {
                return Argument.valueOf(name);
            } catch (IllegalArgumentException e) {
                return null;
            }
        }

        private final WidocoArguments arguments = new WidocoArguments();
        private final ImportProcessContext context;

        private boolean documentAllLanguages = false;

        private WidocoArgumentsBuilder(ImportProcessContext context) {
            this.context = context;
        }

        public void add(Argument argument, JsonNode value) {
            if (WidocoConstants.WIDOCO_DISALLOWED_ARGS.contains(argument)) {
                return;
            }

            if (Argument.FILES.contains(argument) && value.isString()) {
                addFile(argument, value);
            } else if (value.isString() || value.isNumber()) {
                arguments.put(argument, value.asString());
            } else if (value.isBoolean()) {
                add(argument, value.asBoolean());
            } else if (argument.equals(Argument.LANG) && value.isArray()) {
                addLangs(value.asArray().valueStream());
            } else {
                throw JsonFormSubmitException.builder()
                        .errorType(Vocabulary.u_i_form_submit)
                        .internalMessage("Unknown argument: " + argument + " with value " + value.toString())
                        .titleMessageCode("ontopus.core.error.invalidData")
                        .detailMessageArguments(OntopusException.EMPTY_ARGUMENTS)
                        .build();
            }
        }

        public void add(Argument argument, boolean value) {
            if (value) {
                arguments.put(argument, "");
            }
        }

        public void add(String argumentName, JsonNode value) {
            final Argument argument = parseArgument(argumentName);
            if (argument != null) {
                add(argument, value);
            }
        }

        public void addFile(Argument argument, JsonNode value) {
            final Path relative = Path.of(value.asString());
            final Path inContext = FileUtils.resolvePath(context.getTempFolder(), relative);
            arguments.put(argument, inContext.toString());
        }

        protected void addLangs(Stream<JsonNode> langs) {
            if (documentAllLanguages) {
                return;
            }

            Stream<String> languages = langs.filter(JsonNode::isString).map(JsonNode::asString);

            final String param = languages.collect(Collectors.joining("-"));
            if (StringUtils.hasText(param)) {
                arguments.put(Argument.LANG, StringUtils.sanitize(param));
            }
        }

        public WidocoArguments build() {
            return arguments;
        }

        public void documentAllLanguages() {
            addLangs(resolveAlLanguages(context.getTemporaryDatabaseContext()).map(StringNode::new));
            documentAllLanguages = true;
        }
    }
}
