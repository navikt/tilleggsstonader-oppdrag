CREATE TABLE mellomlagring_konsistensavstemming
(
  id                    UUID PRIMARY KEY,
  fagsystem             VARCHAR(4)                              NOT NULL,
  avstemmingstidspunkt  TIMESTAMP(3)                            NOT NULL,
  aktiv                 BOOLEAN         DEFAULT true            NOT NULL,
  antall_oppdrag        INTEGER                                 NOT NULL,
  total_belop           BIGINT                                  NOT NULL,
  opprettet_tidspunkt   TIMESTAMP(3)    DEFAULT localtimestamp  NOT NULL,
  endret_tidspunkt      TIMESTAMP(3)
);

CREATE INDEX mellomlagring_konsistensavstemming_fagsystem_avstemmingstidspunkt_index
        ON mellomlagring_konsistensavstemming (fagsystem, avstemmingstidspunkt);
