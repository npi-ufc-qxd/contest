# language: pt
Funcionalidade: Atribuir Revisores
	
	Eu como organizador preciso distribuir os trabalhos dentre os participantes cadastrados como revisores
		É importante que essa atribuição possa ser feita em partes, ou seja, que não seja obrigatório definir todos os revisores de uma única vez
		É ideal que ao longo da distribuição o organizador tenha fácil acesso as seguintes informações:
		Artigos sem revisores determinados
			Revisores e quantidade de artigos atribuídos para revisão
			O revisor deve ser notificado sobre a necessidade de revisão e o prazo para realizar tal tarefa

	
	Contexto:
		Dado que existe um organizador
        E que existe um evento
		E que o evento possui trilhas de submissão cadastradas
		E a trilha possui um trabalho cadastrado
				
	Cenário: O organizador atribui um revisor a um trabalho que ainda não possui um revisor
        Quando o organizador seleciona atribuir um revisor para um trabalho
        Então o revisor selecionado é atribuído ao trabalho selecionado
		
	Cenário: O organizador remove um revisor de um trabalho que possui um revisor    
        Quando o trabalho tem um revisor
        E o organizador seleciona esse revisor para ser removido do trabalho
        Então o revisor selecionado é removido da lista de revisores do trabalho selecionado