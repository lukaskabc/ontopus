# syntax=docker/dockerfile:1.20

ARG ONTOPUS_SYSTEM_URI_DEFAULT=http://localhost:8080/
ARG ONTOPUS_PERSISTENT_DATA_DIR=/data
ARG ONTOPUS_VERSION=1.0.0-dev

LABEL org.opencontainers.image.source="https://github.com/lukaskabc/ontopus"
LABEL org.opencontainers.image.description="OntoPuS: Ontology Publication Server"
LABEL org.opencontainers.image.licenses="Apache-2.0"

FROM node:25-alpine AS frontend

WORKDIR /administration-frontend
COPY administration-frontend/package.json administration-frontend/package-lock.json* ./
RUN --mount=type=cache,target=/root/.npm \
    npm ci

COPY administration-frontend .

ARG ONTOPUS_SYSTEM_URI_DEFAULT
ENV VITE_ONTOPUS_URL=${ONTOPUS_SYSTEM_URI_DEFAULT}

RUN --mount=type=cache,target=/root/.npm \
    npm run build -- --base=/admin/

FROM maven:3-eclipse-temurin-25-alpine AS backend

WORKDIR /build

COPY .mvn .mvn
COPY mvnw .
COPY "mvnw.cmd" .
COPY pom.xml .
COPY --parents ./*/pom.xml .

ARG ONTOPUS_VERSION

RUN --mount=type=cache,target=/root/.m2 \
    ./mvnw -Drevision=${ONTOPUS_VERSION} -B de.qaware.maven:go-offline-maven-plugin:resolve-dependencies

COPY --exclude=administration-frontend . .

RUN --mount=type=cache,target=/root/.m2 \
    ./mvnw clean package -Drevision=${ONTOPUS_VERSION} -Dspotless.skip -DskipTests

RUN rm ./*/target/original-*.jar

FROM eclipse-temurin:25-jre-alpine as ontopus-base
# OntoPuS Core, Core model and plugin api, with no additional plugins and without frontend

RUN addgroup -S ontopus && adduser -S ontopus -G ontopus
USER ontopus:ontopus

WORKDIR /data
WORKDIR /ontopus
RUN mkdir plugins

ARG ONTOPUS_SYSTEM_URI_DEFAULT
ENV ONTOPUS_SYSTEM_URI=${ONTOPUS_SYSTEM_URI_DEFAULT}
ENV ONTOPUS_DATABASE_URL=http://graphdb:7200/repositories/ontopus
ENV SERVER_FORWARD_HEADERS_STRATEGY=NATIVE
ENV SERVER_PORT=8080

# Change ~/.config to /tmp/xdg_config
ENV XDG_CONFIG_HOME=/tmp/xdg_config

EXPOSE ${SERVER_PORT}/tcp

COPY --from=backend /build/core/target/*.jar /ontopus/core.jar
COPY --from=backend /build/core-model/target/*.jar /ontopus/plugins/
COPY --from=backend /build/plugin-api/target/*.jar /ontopus/plugins/

ENTRYPOINT ["java", "-jar", "./core.jar"]

FROM ontopus-base as ontopus-base-fe
# OntoPuS Core, Core model, plugin API and frontend, with no additional plugins
USER ontopus:ontopus
WORKDIR /ontopus

ENV ONTOPUS_FRONTEND_INDEX_FILE=/ontopus/admin/index.html
COPY --from=frontend /administration-frontend/dist /ontopus/admin

FROM ontopus-base-fe as ontopus
# OntoPuS Core, Core model, plugin API, frontend and all plugins
USER ontopus:ontopus
WORKDIR /ontopus

ARG ONTOPUS_VERSION
ARG ONTOPUS_PERSISTENT_DATA_DIR

ENV ONTOPUS_PLUGIN_WIDOCO_PATH=/data/widoco/
ENV ONTOPUS_PLUGIN_WIDOCO_FILES_DIRECTORY=${ONTOPUS_PERSISTENT_DATA_DIR}/widoco

COPY --from=backend /build/core/target/*.jar /ontopus/core.jar
COPY --from=backend /build/core-model/target/*.jar /ontopus/plugins/
COPY --from=backend /build/plugin-*/target/*.jar /ontopus/plugins/

