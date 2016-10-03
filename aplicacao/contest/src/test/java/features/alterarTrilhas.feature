# language: pt
	Funcionalidade: O organizador altera o nome da trilha de submissão que foi cadastrado erroneamente.

		Eu como organizador gostaria de poder alterar trilhas de submissão.
		O organizador pode alterar o nome da trilha selecionada desde que:
		Nenhum trabalho tenha sido submetido para essa trilha.

			Contexto:
				Dado que o organizador deseja alterar o nome de uma trilha de submissão de um evento
				Quando o organizador selecionar uma trilha para alterar

				Cenário: O organizador altera o nome da trilha de submissão que foi cadastrado erroneamente.
					E a trilha não possuir nenhum trabalho cadastrado
					E o organizador fornecer um novo nome para a trilha
	        Então o nome da trilha é atualizado 

				Cenário: O organizador tenta alterar o nome da trilha de submissão com um nome inválido
					Quando a trilha não possui nenhum trabalho cadastrado
					E não fornece o novo nome da trilha
				  Então o nome da trilha não será atualizado e visualiza uma mensagem de erro

				Cenário: O organizador tenta alterar o nome da trilha de submissão que possui trabalhos submetidos
					Quando a trilha possui algum trabalho cadastrado
					E o organizador fornece um novo nome para a trilha
				  Então o nome da trilha não deve ser atualizado e o organizador deve visualizar uma mensagem de erro
				  
				Cenário: O organizador tenta alterar o nome da trilha para um nome que já existe
					E o organizador fornece um novo nome que já existe
					Então o organizador deve visualizar uma mensagem de erro