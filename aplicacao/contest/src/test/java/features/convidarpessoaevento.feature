# language: pt
Funcionalidade:  O organizador convida participantes 
	
	Eu como organizador posso convidar  um participante para um evento.
	O formulário de convite deve conter os seguintes campos: Nome do Convidado, Função e Email do Convidado	sendo todos campus obrigatórios.
	O organizador deve ser um servidor cadastrado no sistema
	O evento deve estar com status ativo inativo
	
      Contexto:
        Dado o organizador deseja convidar um participante para evento.
	
	  Cenário: Convite para evento enviado com sucesso
	  	Quando selecionar o evento Teste com nome do convidado Robson Funcao Organizador e email exemplo@gmail.com
	  	Então um email deve ser enviado com sucesso
		
	  Cenário: Enviar email sem preencher o campo Email do Convidado
		Quando informar campo email vazio para evento Teste com nome do convidado Robson Funcao Organizador
		Então O email nao deve ser enviado para o participante
		
	  Cenário: Enviar convite para email com formato inválido
	  	Quando informar o nome Zaras e a Função Revisor e o email  exemplo Incorreto@@mail.com para evento Teste
	  	Então O convite nao deve ser enviado
	  	 