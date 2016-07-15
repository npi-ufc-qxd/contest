# language: pt
Funcionalidade: Editar Evento
	Eu como administrador gostaria de alterar um evento já cadastrado para atualizar ou corrigir informações
	O administrador pode alterar o nome, a descrição e o organizador
	O administrador só poderá alterar eventos que esteja com status inativo
	
	Contexto:
		Dado que o administrador deseja alterar um evento
	
	Cenário: Alterar os detalhes de um evento inativo
		Quando altero o nome de um evento para NOME e descrição para DESCRICAO
		Então as configurações do evento são alteradas

	Cenário: Alterar um evento ativo
		Quando o evento escolhido é um evento ativo com nome NOME decrição DESCRICAO organizador JOAO
		Então o evento não deve ser alterado
		E o usuário é avisado via mensagem o motivo do insucesso do cadastro
	
	Cenário: Alterar um evento inativo sem informar o organizador do evento
		Quando edito o nome do evento para NOMEEDITADO e descricao para NOVADESCRICAO e não informo o nome do organizador
		Então o usuário é avisado via mensagem que o organizador do evento deve ser informado

	Cenário: Alterar o organizador de um evento inativo para por um organizador não cadastrado
		Quando edito o nome do evento para NOMEEDITADO e descricao para NOVADESCRICAO e escolho um organizador não cadastrado
		Então deve ser mostrado uma mensagem dizendo que o oganizadr não foi encontrado 
