CREATE TABLE secao (
    id bigint NOT NULL,
    nome character varying(255),
    local character varying(255),
    descricao character varying(255),
    responsavel_id bigint, 
    Primary key(id)
);

ALTER TABLE trabalho ADD COLUMN secao_id bigint;

ALTER TABLE trabalho
   ADD CONSTRAINT secao_id_key
   FOREIGN KEY (secao_id) 
   REFERENCES secao(id);

ALTER TABLE secao
   ADD CONSTRAINT responsavel_id_key
   FOREIGN KEY (responsavel_id) 
   REFERENCES pessoa(id);
