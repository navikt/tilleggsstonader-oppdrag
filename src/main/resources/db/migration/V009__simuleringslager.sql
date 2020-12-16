CREATE TABLE simulering_lager
(
  id                    UUID PRIMARY KEY,
  fagsak_id             VARCHAR NOT NULL,
  behandling_id         VARCHAR NOT NULL,
  fagsystem             VARCHAR NOT NULL,
  opprettet_tidspunkt   timestamp(3) NOT NULL DEFAULT localtimestamp,
  utbetalingsoppdrag    text NOT NULL,
  request_xml           text NOT NULL,
  response_xml          text
);

CREATE INDEX simuleringsid_idx ON simulering_lager (behandling_id, fagsak_id, fagsystem);
