# language: pt
	Funcionalidade: Excluir Evento
	
	Eu como administrador gostaria de excluir um evento inativo
	Ao solicitar a exclusão, o usuário deverá confirmar a solicitação de exclusão
	
	Contexto:
		Dado o administrador deseja excluir um evento
	
				
	Cenário: Listar eventos inativos
		Quando ele selecionar a lista de eventos inativos
		Então todos os eventos inativos devem ser exibidos
		
	Cenário: Excluir um evento inativo com sucesso.    
		Quando removo um evento com id 1
		Então evento deve ser excluido com sucesso
		
	Esquema do Cenário: Excluir um evento que não esta inativo.
		Quando tento remover um evento com estado <entrada> e id 1
		Então o usuário é informado que não pode excluir esse evento
				
		Exemplos:
			| entrada    |
			| FINALIZADO |
			| ATIVO      |