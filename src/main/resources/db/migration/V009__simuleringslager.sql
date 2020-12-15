CREATE TABLE simulerings_lager
(
  id                    UUID PRIMARY KEY,
  fagsak_id             VARCHAR(50) NOT NULL,
  behandling_id         VARCHAR(50) NOT NULL,
  fagsystem             VARCHAR(10) NOT NULL,
  opprettet_tidspunkt   timestamp(3) NOT NULL DEFAULT localtimestamp,
  utbetalingsoppdrag    json NOT NULL,
  request_xml           text NOT NULL,
  response_xml          text
);

CREATE INDEX simuleringsid_idx ON simulerings_lager (behandling_id, fagsak_id, fagsystem);
