CREATE TABLE secao (
    id bigint NOT NULL,
    nome character varying(255),
    local character varying(255),
    descricao character varying(255),
    pessoa_id bigint, 
    Primary key(id)
);

ALTER TABLE trabalho ADD COLUMN secao_id bigint;

ALTER TABLE trabalho
   ADD CONSTRAINT secao_id_key
   FOREIGN KEY (secao_id) 
   REFERENCES secao(id);

ALTER TABLE secao
   ADD CONSTRAINT pessoa_id_key
   FOREIGN KEY (pessoa_id) 
   REFERENCES pessoa(id);
