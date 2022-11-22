FROM navikt/java:17-appdynamics

ENV APPD_ENABLED=TRUE
ENV APP_NAME=familie-oppdrag

COPY init.sh /init-scripts/init.sh
COPY ./target/familie-oppdrag.jar "app.jar"
