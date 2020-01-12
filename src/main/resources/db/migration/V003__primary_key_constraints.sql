ALTER TABLE oppdrag_protokoll
    DROP CONSTRAINT oppdrag_protokoll_pkey,
    ADD PRIMARY KEY (person_ident,behandling_id,fagsystem);