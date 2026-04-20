| Variable | Description |
| --- | --- |
| ```ONTOPUS_ADMINISTRATION_ALLOWED_ORIGINS```**\*** | Allowed origins for administration endpoints<br>Default value: ```the SYSTEM_URI value is used```<br>value must be present and not empty |
| ```ONTOPUS_DATABASE_DRIVER```**\*** | JOPA OntoDriver implementation class<br>See: <a href="https://github.com/kbss-cvut/jopa/wiki/OntoDriver">JOPA OntoDriver</a><br>Default value: ```cz.cvut.kbss.ontodriver.rdf4j.Rdf4jDataSource```<br>value must be present |
| ```ONTOPUS_DATABASE_LANGUAGE```**\*** | The default persistence language<br>Default value: ```en```<br>value must be present |
| ```ONTOPUS_DATABASE_URL```**\*** | Database repository URL<br>Default value: ```http://localhost:7200/repositories/ontopus```<br>value must be present |
| ```ONTOPUS_PLUGIN_WIDOCO_EXECUTION_TIMEOUT```**\*** | Default value: ```Duration.ofMinutes(10)```<br>value must be present |
| ```ONTOPUS_PLUGIN_WIDOCO_FILES_DIRECTORY```**\*** | Persistent directory for generated files<br>value must be present |
| ```ONTOPUS_PLUGIN_WIDOCO_PATH```**\*** | Path to widoco executable jar<br>value must be present |
| ```ONTOPUS_SYSTEM_URI```**\*** | The URI dedicated for the OntoPuS. Base paths are not supported. Usually you want to keep HTTP protocol and<br>replace the domain with dedicated subdomain for the server. Example: ```http://example.com```<br>Default value: ```URI.create("http://localhost")```<br>value must be present |
| ```ONTOPUS_DATABASE_PASSWORD``` | Password for authentication with database repository |
| ```ONTOPUS_DATABASE_USERNAME``` | Username for authentication with database repository |
| ```ONTOPUS_DCAT_CATALOG_DESCRIPTION``` | Description of the catalog<br>Default value: ```Catalog of published ontologies on this OntoPuS instance``` |
| ```ONTOPUS_DCAT_CATALOG_LANGUAGE``` | Language of the catalog metadata (title, description).<br>Default value: ```null``` |
| ```ONTOPUS_DCAT_CATALOG_TITLE``` | Title of the catalog<br>Default value: ```OntoPuS Ontology Catalog``` |
| ```ONTOPUS_DCAT_CATALOG_URI``` | Default value: ```URI.create("http://localhost/ontopus/catalog")``` |
| ```ONTOPUS_DEFAULT_MAX_PAGE_SIZE``` | Default value: ```100``` |
| ```ONTOPUS_FILES_DEFAULT_GLOB_PATTERN``` | Default value: ```**.{nt,rdf,ttl,trig,trigs,brf,ttls}``` |
| ```ONTOPUS_FILES_IMPORT_FILES_DIRECTORY``` | Directory for storing files used with ontology importing.<br>Default value: ```./``` |
| ```ONTOPUS_FRONTEND_INDEX_FILE``` |  |
| ```ONTOPUS_PLUGIN_GIT_TIMEOUT``` | Default value: ```15``` |
| ```ONTOPUS_PLUGIN_WIDOCO_DOWNLOAD_URL``` | Default value: ```https://github.com/dgarijo/Widoco/releases/download/v{version}/widoco-{version}-jar-with-dependencies_JDK-17.jar``` |
| ```ONTOPUS_PLUGIN_WIDOCO_DOWNLOAD_URL_PARAMETERS``` | Widoco version to automatically download<br>Default value: ```Map.of("version", "1.4.25")``` |
| ```ONTOPUS_RESOURCE_CACHE_CONTROL_MAX_AGE``` | The value of ```max-age``` in cache control HTTP header<br>Default value: ```Duration.ofDays(1)``` |
| ```ONTOPUS_RESOURCE_HTTPS_FALLS_BACK_TO_HTTP``` | Request to ```https``` prefixed resource that does not exist, will fall back to the same resource with ```http```.<br>Default value: ```true``` |
| ```ONTOPUS_RESOURCE_HTTP_FALLS_BACK_TO_HTTPS``` | Request to ```http``` prefixed resource that does not exist, will fall back to the same resource with ```https```.<br>Default value: ```false``` |
| ```ONTOPUS_RESOURCE_NO_SLASH_FALLS_BACK_TO_TRAILING_SLASH``` | Request to resource without trailing slash that does not exist, will fall back to the same resource with<br>trailing slash.<br>Default value: ```true``` |
| ```ONTOPUS_RESOURCE_TRAILING_SLASH_FALLS_BACK_TO_NO_SLASH``` | Request to resource with trailing slash that does not exist, will fall back to the same resource without<br>trailing slash.<br>Default value: ```true``` |

**\* Required**
