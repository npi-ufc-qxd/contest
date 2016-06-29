# language: pt
	Funcionalidade: Alterar Evento Inativo
	
	Eu como administrador gostaria de alterar um evento já cadastrado para atualizar ou corrigir informações
	O administrador pode alterar o nome, a descrição e o organizador
	O administrador só poderá alterar eventos que esteja com status inativo
	
		Cenário: Alterar um evento que não existe
		Dado que existe um administrador
		Quando o administrador tenta alterar um evento que não existe
		Então uma mensagem de erro é retornada
	
		Cenário: Alterar o nome de um evento inativo
		Dado que o administrador deseja alterar um evento
		E o evento escolhido é um evento inativo de nome EventoTeste
		Quando um novo nome NovoTeste do evento é informado
		Então os dados do evento devem ser atualizados