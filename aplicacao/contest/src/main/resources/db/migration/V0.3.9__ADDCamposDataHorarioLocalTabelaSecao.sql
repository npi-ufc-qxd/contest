ALTER TABLE secao ADD data_secao character varying(255);
ALTER TABLE secao ADD horario character varying(255);
ALTER TABLE secao ADD local character varying(255);
ALTER TABLE secao DROP COLUMN descricao;