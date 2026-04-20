# Deployment
The application consist of a single Java Spring Boot application and PReact frontend, which is served directly by the Java application.
Deployment is realized using Docker and Docker Compose.

## Prerequisites
- Subdomain dedicated for the OntoPuS application (e.g. ontopus.example.com)
  - The subdomain hosts the administration frontend, administration API and some public endpoints (e.g. Widoco generated HTML documentation for the ontologies).
  - The subdomain **CAN NOT** be replaced with a path (e.g. example.com/ontopus), subdomain is required
- [Optional] Existing [GraphDB](https://graphdb.ontotext.com/) server
- [Optional] Existing reverse proxy server (e.g. Nginx, Apache) for SSL termination and routing

## Docker compose

In order to deploy the application, proceed with the following steps:
1. Clone the repository and navigate to the root directory
```bash
git clone https://github.com/lukaskabc/ontopus.git --depth 1
cd ontopus
```
2. Copy the example environment file and edit it according to your needs
```bash
cp .env.template .env
```
see the [configuration documentation](./CONFIGURATION.md) for more details on the available configuration options.

Example:
```properties
ONTOPUS_SYSTEM_URI=http://ontopus.lukaskabc.cz
ONTOPUS_PLUGIN_WIDOCO_FILES_DIRECTORY=/tmp/widoco
ONTOPUS_PLUGIN_WIDOCO_PATH=${ONTOPUS_PLUGIN_WIDOCO_FILES_DIRECTORY}
ONTOPUS_FRONTEND_INDEX_FILE=./administration-frontend/dist/index.html
ONTOPUS_DATABASE_URL=http://graphdb:7200/repositories/ontopus
# Enable processing of forwarded headers (e.g. X-Forwarded-For) by TomCat
SERVER_FORWARD_HEADERS_STRATEGY=NATIVE
SERVER_PORT=8080
```

3. Edit the docker compose file
- If you have already a GraphDB server, add external docker network to the compose and connect the ontopus container to it. Then configure the database URL in .env file.
  - If you dont have an existing GraphDB server, uncomment the ports in the compose file for GraphDB to access the configuration interface later.
- If you already have a reverse proxy server, you can remove the nginx container, you can check example configuration for Nginx in [docker/nginx](./docker/nginx) directory.
  - Usually you will want to configure system URI for HTTPs only and then all the domains of your ontologies for HTTP and HTTPs

4. Build the stack and start the containers
```bash
docker compose up --build -d
```

5. Configure GraphDB server
- Access the interface of the GraphDB server (e.g. http://localhost:7200)
- Create a new repository with the same name as in the configuration (default: `ontopus`)
- Repository parameters:
```properties
GraphDB repository
Repository ID: ontopus
Ruleset: No inference
Disable owl:sameAs: true
Enable context index: true
```

6. Edit the docker compose file to remove the exposed ports for GraphDB
7. Restart the stack
```bash
docker compose down
docker compose up -d
```
8. Check ontopus logs, a new admin user should be created
9. Access the administration interface at the system URI (e.g. http://ontopus.lukaskabc.cz/admin) and log in with the created user

## Changing the user account password
The password value uses bcrypt hash.
The hash can be generated for example with [CyberChef](https://cyberchef.org/#recipe=Bcrypt(10)&input=YWJlY2VkYQ).
```sparql
PREFIX onto: <http://ontology.lukaskabc.cz/application/ontopus/>
DELETE {
    onto:UserAccount_admin onto:password ?oldValue .
}
INSERT {
    GRAPH <http://ontology.lukaskabc.cz/application/ontopus/UserAccount> {
        onto:UserAccount_admin onto:password "$2a$10$IHT7VOVBS0w6vHuz5OB/e.iBAqRnvwT8jJuDHfRfrukadXEn8Djlq" .
    }
}
WHERE {
    GRAPH <http://ontology.lukaskabc.cz/application/ontopus/UserAccount>{
        onto:UserAccount_admin onto:password ?oldValue .
    }
}
```

This is a temporary solution until user management is properly implemented.
