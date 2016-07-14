# language: pt
	Funcionalidade: Alterar Evento Inativo
	
	Eu como administrador gostaria de alterar um evento já cadastrado para atualizar ou corrigir informações
	O administrador pode alterar o nome, a descrição e o organizador
	O administrador só poderá alterar eventos que esteja com status inativo
	
	Contexto:
		Dado que o administrador deseja alterar um evento
		
	Cenário: Requisitar a alteração de evento    
		Quando escolho alterar um evento com nome EVENTO, descrição DESCRICAO e organizador ORGANIZADOR  
		Então devo ser redirecionado para a pagina de formulario
		
	Cenário: Alterar os detalhes de um evento inativo
		Quando tento alterar o nome de um evento para NOME e descrição para DESCRICAO
		Então as configurações do evento são alteradas

	Cenário: Alterar um evento ativo
		Quando o evento escolhido é um evento ativo
		Então o evento não deve ser alterado
		E o usuário é avisado via mensagem o motivo do insucesso do cadastro