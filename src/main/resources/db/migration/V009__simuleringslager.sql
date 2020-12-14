CREATE TABLE CREATE TABLE simulerings_lager
(
  person_ident          VARCHAR(50) NOT NULL,
  fagsak_id             VARCHAR(50) NOT NULL,
  behandling_id         VARCHAR(50) NOT NULL,
  fagsystem             VARCHAR(10) NOT NULL,
  opprettet_tidspunkt   timestamp(3) NOT NULL DEFAULT localtimestamp,
  utbetalingsoppdrag    json NOT NULL,
  request_xml           text NOT NULL,
  response_xml          text,
  versjon               BIGINT NOT NULL DEFAULT 0,
);

CREATE INDEX simuleringsid_idx ON simulerings_lager (behandling_id, fagsak_id, versjon);

ALTER TABLE simulerings_lager
    ADD CONSTRAINT simulerings_lager_pkey PRIMARY KEY (fagsak_id, behandling_id, fagsystem, versjon);
