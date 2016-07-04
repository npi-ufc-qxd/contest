# language: pt
	Funcionalidade: Alterar Evento Inativo
	
	Eu como administrador gostaria de alterar um evento já cadastrado para atualizar ou corrigir informações
	O administrador pode alterar o nome, a descrição e o organizador
	O administrador só poderá alterar eventos que esteja com status inativo
	
	Cenário: Dado o administrador deseja altera o nome, descrição ou organizador de um evento.
		Quando altero o nome de um evento Teste
		Então o nome do evento é alterado
		Quando altero a descrição do evento
		Então a descrição é alterada
		Quando altero o organizador do evento
		Então o organizador é alterado
		
	
	Cenário: Alterar um evento que não existe
		Dado que existe um administrador
		Quando o administrador tenta alterar um evento que não existe
		Então uma mensagem de erro é retornada
	