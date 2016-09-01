# language: pt
Funcionalidade: O Aluno participa de um evento
	
	Eu como aluno gostaria de participar de um evento.
	O aluno deve ser capaz de buscar somente por eventos ativos.
	Apenas eventos públicos devem aparecer.
	O aluno deve escolher o evento e confirmar sua inscrição.
	
	  Cenário: O aluno decide participar de um evento ativo e público
	 	Dado que existe um evento ativo e público cadastrado no sistema
		Quando o aluno decide participar de um evento com id 2
		Então o sistema registra a participação
		E retorna uma mensagem de sucesso para o aluno
			
	  Cenário: O aluno decide participar de um evento inativo
		Dado que existe um evento inativo cadastrado no sistema com id 1
		Quando o aluno decide participar deste evento
		Então uma mensagem de erro é retornada dizendo que o aluno não pode participar de eventos inativos
		
	  Cenário: O aluno recebe convite para participar de evento privado
		Dado que existe um evento privado e ativo cadastrado no sistema
		E o organizador deste evento convida o aluno para participar
		Então o aluno é notificado do convite de participação
		Quando o aluno aceitar o convite
		Então o sistema registra a participação
		E o organizador do evento é notificado sobre participação do aluno
		E retorna uma mensagem de sucesso para o aluno