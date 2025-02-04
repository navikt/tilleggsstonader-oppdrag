FROM ghcr.io/navikt/baseimages/temurin:17

ENV APPLICATION_NAME=tilleggsstonader-oppdrag

EXPOSE 8080
COPY build/libs/*.jar ./

ENV JAVA_OPTS="-XX:MaxRAMPercentage=75"
