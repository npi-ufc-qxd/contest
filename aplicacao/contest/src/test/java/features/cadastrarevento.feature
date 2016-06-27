# language: pt
	Funcionalidade: Cadastrar Evento
	
	Eu como administrador posso criar um evento e atribuir um organizador.
	O formulário de cadastro deve conter os seguintes campos: Nome do Evento, Descrição e Organizador do Evento
	sendo nome do evento e organizador campos obrigatórios.
	O organizador deve ser um servidor cadastrado no sistema
	O projeto recém cadastrado deve permanecer com status inativo e visibilidade privado (apenas o admin e o organizador pode visualizar)
	
	  Cenário: Cadastro de evento com sucesso
		Dado o administrador deseja cadastrar um evento.
		Quando informa o organizador Lucas e o nome do evento Teste
		Então o evento deve ser cadastrado com visibilidade privada e estado inativo.