
ALTER TABLE secao ADD COLUMN evento_id bigint;

ALTER TABLE secao
   ADD CONSTRAINT evento_id_key
   FOREIGN KEY (evento_id) 
   REFERENCES evento(id);