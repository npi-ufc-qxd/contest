# language: pt
	Funcionalidade: O organizador exclui uma trilha de submissão previamente cadastrada.

		Eu como organizador gostaria de poder excluir uma trilha de submissão
			O organizador pode excluir a trilha selecionada desde que:
				Nenhum trabalho tenha sido submetido para essa trilha

	Contexto:
		Dado que o organizador deseja excluir uma trilha de submissão de um evento
		
		Cenário: O organizador exclui uma trilha de submissão e não existe um trabalho submetido
			E não existe nenhum trabalho submetido para essa trilha
			Quando o organizador seleciona uma trilha
	    Então a trilha é excluída
	    
	   Cenário: O organizador exclui uma trilha de submissão e existe um trabalho submetido
			E existe algum trabalho submetido para essa trilha
			Quando o organizador seleciona uma trilha sem trabalho
	    Então retorna uma mensagem de erro