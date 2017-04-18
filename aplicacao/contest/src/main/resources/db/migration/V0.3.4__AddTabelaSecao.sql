CREATE TABLE secao (
    idSecao bigint NOT NULL,
    nome character varying(255),
    local character varying(255),
    descricao character varying(255),
    pessoa_id bigint, 
    Primary key (idSecao)
);

ALTER TABLE trabalho ADD secao_id bigint;

ALTER TABLE trabalho
ADD FOREIGN KEY (secao_id) REFERENCES secao(idSecao);

ALTER TABLE secao
ADD FOREIGN KEY (pessoa_id) REFERENCES pessoa(id);