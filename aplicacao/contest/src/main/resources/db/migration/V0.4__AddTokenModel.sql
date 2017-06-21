CREATE TABLE token (
    token character varying(255) NOT NULL,
    acao character varying(255),
    pessoa_id bigint
);
ALTER TABLE ONLY token
    ADD CONSTRAINT token_pkey PRIMARY KEY (token);

ALTER TABLE token
   ADD CONSTRAINT pessoa_id_key
   FOREIGN KEY (pessoa_id) 
   REFERENCES pessoa(id);