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

To use a prebuild image, use the [docker/docker-compose.yaml](./docker/docker-compose.yaml) example.
See the [configuration documentation](./CONFIGURATION.md) for more details on the available configuration options.


1. Edit the docker compose file
- If you have already a GraphDB server, add external docker network to the compose and connect the ontopus container to it. Dont forget to change the database URL (`ONTOPUS_DATABASE_URL`).
  - If you dont have an existing GraphDB server, uncomment the ports in the compose file for GraphDB to access the configuration interface later.

- If you already have a reverse proxy server, you can remove the nginx container, you can check example configuration for Nginx in [docker/nginx](./docker/nginx) directory.
  - You need to configure the subdomain for ontopus and all the domains of your ontologies.

- If you want to use the nginx proxy in the example docker file, you will need the whole [docker/nginx](./docker/nginx) directory.

2. Build the stack and start the containers
```bash
docker compose up -d
```

3. Configure GraphDB server
- Access the interface of the GraphDB server (e.g. http://localhost:7200)
- Create a new repository with the same name as in the configuration (default: `ontopus`)
- Repository parameters:
```
GraphDB repository
Repository ID: ontopus
Ruleset: No inference
Enable context index: true
Enable SHACL validation: true
```

4. Edit the docker compose file to remove the exposed ports for GraphDB
5. Restart the stack
```bash
docker compose down
docker compose up -d
```
6. Check ontopus logs, a new admin user should be created
```
No user account found. Generated new account: admin, password: <password>
Make sure to change the password after the first login!
```
7. Access the administration interface at the system URI (e.g. http://ontopus.lukaskabc.cz/admin) and log in with the created user

## Changing the user account password
The password value uses bcrypt hash.
The hash can be generated for example with [CyberChef](https://cyberchef.org/#recipe=Bcrypt(10)&input=YWJlY2VkYQ).
```sparql
PREFIX ontopus: <http://ontology.lukaskabc.cz/application/ontopus/>
PREFIX ontopus_user: <http://ontology.lukaskabc.cz/application/ontopus/UserAccount/>
DELETE {
    ontopus_user:admin ontopus:password ?oldValue .
}
INSERT {
    GRAPH ontopus:UserAccount {
        ontopus_user:admin ontopus:password "<bcrypt_hash>" .
    }
}
WHERE {
    GRAPH ontopus:UserAccount {
        ontopus_user:admin ontopus:password ?oldValue .
    }
}
```

To add a new user:
(update the identifier, username and password hash)
`<http://ontology.lukaskabc.cz/application/ontopus/UserAccount/USERNAME>`
```sparql
INSERT DATA {
  GRAPH <http://ontology.lukaskabc.cz/application/ontopus/UserAccount> {
    <http://ontology.lukaskabc.cz/application/ontopus/UserAccount/USERNAME> a <http://ontology.lukaskabc.cz/application/ontopus/UserAccount> ;
      <http://ontology.lukaskabc.cz/application/ontopus/password> "<brypt_hash>" ;
      <http://rdfs.org/sioc/ns#name> "<USERNAME>" .
  }
}
```

This is a temporary solution until user management is properly implemented.
