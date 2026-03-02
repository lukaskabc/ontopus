# syntax=docker/dockerfile:1.20
FROM node:25-alpine AS frontend

WORKDIR /administration-frontend
COPY administration-frontend/package.json administration-frontend/package-lock.json* ./
RUN --mount=type=cache,target=/root/.npm \
    npm ci

COPY administration-frontend .

ENV VITE_ONTOPUS_URL=http://ontopus.local:8080/

RUN --mount=type=cache,target=/root/.npm \
    npm run build -- --base=/admin/

FROM maven:3-eclipse-temurin-25-alpine AS backend

WORKDIR /build

COPY .mvn .mvn
COPY mvnw .
COPY "mvnw.cmd" .
COPY pom.xml .
COPY --parents ./*/pom.xml .

RUN --mount=type=cache,target=/root/.m2 \
    ./mvnw dependency:go-offline -B

COPY --exclude=administration-frontend . .

RUN --mount=type=cache,target=/root/.m2 \
    ./mvnw clean package -Dspotless.skip -DskipTests

FROM eclipse-temurin:25-jre-alpine as ontopus

RUN addgroup -S ontopus && adduser -S ontopus -G ontopus
USER ontopus:ontopus

WORKDIR /ontopus
RUN mkdir plugins

ENV ONTOPUS_FRONTEND_INDEX_FILE=/ontopus/admin/index.html

COPY --from=backend /build/core/target/*.jar /ontopus/core.jar
COPY --from=backend /build/core-model/target/*.jar /ontopus/plugins/
COPY --from=backend /build/plugin-*/target/*.jar /ontopus/plugins/

COPY --from=frontend /administration-frontend/dist /ontopus/admin

RUN ls -la --recursive /ontopus

ENTRYPOINT ["java", "-jar", "./core.jar"]
