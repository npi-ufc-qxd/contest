alter table evento drop column prazo_revisao;
alter table evento drop column prazo_submissao;
alter table evento add prazo_revisao_inicial date;
alter table evento add prazo_revisao_final date;
alter table evento add prazo_submissao_inicial date;
alter table evento add prazo_submissao_final date;