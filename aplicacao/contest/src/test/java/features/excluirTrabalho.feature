# language: pt
Funcionalidade: Excluir Trabalho
	
	Eu como administrador gostaria de excluir um evento inativo
	Ao solicitar a exclusão, o usuário deverá confirmar a solicitação de exclusão
	
	Contexto:
		Dado que o aluno deseja remover seu trabalho
				
	Cenário: Excluir trabalho com sucesso.
		E o prazo de submissão não acabou
		Quando seleciona o trabalho que desejo excluir
		Então o trabalho deve ser excluído
		E uma mensagem de sucesso deve ser informada
			
	Cenário: Excluir trabalho fora do prazo de submissão.    
		E está fora do prazo de submissão
		Quando seleciona o trabalho que deverá ser excluir
		Então o trabalho não deve ser excluído
		E uma mensagem deve ser mostrada ao usuário