* Må legge inn service-user på en eller annen måte
* Finne ut om vi kan kalle på disse fra GCP, eller om vi trenger en proxy
  * `OPPDRAG_SERVICE_URL: "https://cics-q1.adeo.no/oppdrag/simulerFpServiceWSBinding"`
  * `SECURITYTOKENSERVICE_URL: https://sts-q1.preprod.local/SecurityTokenServiceProvider/`

* Slå sammen migreringsscript til databasen
* Forbedre databas-scheman

* Ta inn utbetalingsgeneneratorn
* Lage en kontrakt for å sende andeler, uten kjeder
* Lage endepunkt for å iverksette/simulere der man plukker opp forrige kjeder
  * Tabell for iverksatte perioder

* Fjerne avhengighet til familie-kontrakt, familie-felles
* Verifisere at appen starter, readiness/ burde kontrolleres og kanskje endres i nais-filer

* Fjerne interfaces? Og kun ha klassene
* Fjerne E2E klasser