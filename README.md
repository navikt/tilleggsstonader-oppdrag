# tilleggsstonader-oppdrag
Iverksetting og avstemming mot OS for tilleggsstønader

## Bygging
Bygging gjøres med `./gradlew build`. 

## Swagger
http://localhost:8087/swagger-ui.html

## Kjøring lokalt
#### Sett environment variabel
```
AZURE_APP_CLIENT_ID=<client id i tilleggsstonader-oppdrag i preprod>
```
### Kjøring der app'en starter containere for postgres og MQ
For å kjøre opp appen lokalt kan en kjøre `DevPsqlMqLauncher`, som har spring-profilen `dev_psql_mq` satt.
Appen vil starte containere for siste versjoner av PostgresSql og IBM MQ.

Appen tilgjengeliggjøres på `localhost:8087`.

### Kjøring med separate containere for postgres og MQ
For å kjøre opp appen lokalt kan en kjøre `DevLauncher`, som har spring-profilen `dev` satt.
Appen tilgjengeliggjøres på `localhost:8087`.

I tillegg må man kjøre opp en MQ-container med docker:
```
docker run \
  --env LICENSE=accept \
  --env MQ_QMGR_NAME=QM1 \
  --publish 1414:1414 \
  --publish 9443:9443 \
  --detach \
  ibmcom/mq
```

Og sette opp en database lokalt:
```
docker run --name tilleggsstonader-oppdrag -e POSTGRES_PASSWORD=test -d -p 5432:5432 postgres
docker ps (finn container id)
docker exec -it <container_id> bash
psql -U postgres
CREATE DATABASE "tilleggsstonader-oppdrag";
```

Les mer om postgres på nav [her](https://github.com/navikt/utvikling/blob/master/PostgreSQL.md). For å hente credentials manuelt, se [her](https://github.com/navikt/utvikling/blob/master/Vault.md). 

## Teste i preprod, f.eks Postman

Detaljer for å få access-token ligger [her](https://github.com/navikt/familie/blob/master/doc/utvikling/preprod/kalle_autentisert_api.md).

## Kikke på database i preprod
Detaljer ligger [her](https://github.com/navikt/familie/blob/master/doc/utvikling/preprod/kikke_i_databasen)


## Kontaktinfo
For NAV-interne kan henvendelser om appen rettes til #tilleggsstonader-dev på slack. Ellers kan man opprette et issue her på github.
