# syntax=docker/dockerfile:1.20
FROM node:25-alpine AS frontend

WORKDIR /administration-frontend
COPY administration-frontend .
RUN npm ci
RUN npm run build -- --base=/admin/

FROM maven:3-eclipse-temurin-25-alpine AS backend

WORKDIR /build

COPY .mvn .mvn
COPY mvnw .
COPY "mvnw.cmd" .
COPY pom.xml .
COPY --parents ./*/pom.xml .

RUN ls -la --recursive .

RUN ./mvnw clean verify --fail-never

COPY --exclude=administration-frontend . .
COPY --from=frontend /administration-frontend/dist ./core/src/main/resources/static

RUN ./mvnw clean package -Dspotless.skip

FROM eclipse-temurin:25-jre-alpine as ontopus

RUN addgroup -S ontopus && adduser -S ontopus -G ontopus
USER ontopus:ontopus

WORKDIR /ontopus
RUN mkdir plugins

COPY --from=backend /build/core/target/*.jar /ontopus/core.jar
COPY --from=backend /build/core-model/target/*.jar /ontopus/plugins/
COPY --from=backend /build/plugin-*/target/*.jar /ontopus/plugins/

RUN ls -la --recursive /ontopus

ENTRYPOINT ["java", "-jar", "./core.jar"]
