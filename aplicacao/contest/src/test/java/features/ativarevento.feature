# language: pt
Funcionalidade: Ativar Evento
	Eu como organizador gostaria de configurar os detalhes do evento e ativá-lo
	Para ativar um evento o organizador deve informar obrigatoriamente os seguintes campos: 
	Nome do evento(Deve vir previamente preenchido com os dados informados pelo administrador)administrador)
	Prazo de submissão (Data Inicial e Data Final, Di < Df)
	Prazo de revisão (Data Inicial e Data Final, Di < Df)
	Os prazos de revisão devem iniciar depois do prazo final de submissão
	visibilidade: público ou privado
	
	Contexto:
		Dado que o organizado deseja ativar um evento com o id 1
	
	Esquema do Cenário: O organizador configura um evento de forma correta
        Quando o organizador configura o evento para a data de submissao inicial para 10-06-2020, data final de submissao para 28-06-2020, data de revisão inicial para 15-06-2020 e data de revisão final para 25-06-2020 e visibilidade <visibilidade>
        Então o evento é ativado
		Exemplos:
			| visibilidade |
			| PUBLICO      |
			| PRIVADO      |

	Esquema do Cenário: O tenta ativar um evento sem nome 
        Quando o organizador apaga o campo nome e ativa o evento com visibilidade <visibilidade>
        Então uma mensagem de erro no nome é retornada ao organizador
		Exemplos:
			| visibilidade |
			| PUBLICO      |
			| PRIVADO      |