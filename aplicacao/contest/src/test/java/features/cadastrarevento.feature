# language: pt
Funcionalidade: Cadastrar Evento
	
	Eu como administrador posso criar um evento e atribuir um organizador.
	O formulário de cadastro deve conter os seguintes campos: Nome do Evento, Descrição e Organizador do Evento
	sendo nome do evento e organizador campos obrigatórios.
	O organizador deve ser um servidor cadastrado no sistema
	O projeto recém cadastrado deve permanecer com status inativo e visibilidade privado (apenas o admin e o organizador pode visualizar)
	
	  Cenário: Cadastro de evento com sucesso
		Dado o administrador deseja cadastrar um evento.
		Quando informar o organizador Lucas e o evento com nome Teste e descricao Legal
		Então o evento deve ser cadastrado com visibilidade privada e estado inativo.
		
	  Cenário: Cadastrar evento sem informar o organizador do evento
 	  	Dado que o administrador deseja cadastrar um evento no sistema
		Quando informar somente o nome do evento Teste
		Então O evento não deve ser cadastrado
		
	  Cenário: Cadastro de evento com organizador não cadastrado no sistema
	  	Dado que o administrador deseja cadastrar um evento no sistema.
		Quando informar o organizador Bruno e o nome evento Teste
		E o organizador do evento informado não está cadastrado no sistema
		Então O evento não deve ser cadastrado no sistema
		
	  Cenário: Cadastro de evento sem nome
	  	Dado que o administrador deseja cadastrar um evento no sistema contest
		Quando informar somente o organizador Matheus
		Então O evento não deve ser cadastrado devido ao nome vazio