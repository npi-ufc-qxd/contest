# language: pt
Funcionalidade:  O organizador convida participantes 
	
	Eu como organizador posso convidar  um participante para um evento.
	O formulário de convite deve conter os seguintes campos: Nome do Convidado, Função e Email do Convidado	sendo todos campus obrigatórios.
	O organizador deve ser um servidor cadastrado no sistema
	O evento deve estar com status ativo
        
     Cenário: O organizador convida pessoas para participarem de um evento ativo
        Dado que existe um evento ativo
        E que existe um organizador
        E que o organizador especifica o papel AVALIADOR do convidado
        Quando o organizador convida a pessoa com nome FULANO e email fulano@gmail.com para participar do evento
        Então um convite por email é enviado para a pessoa
        
     Cenário: O organizador enviar convite para email com formato inválido        
        Dado que existe um evento com estado ativo
        E que existe um organizador cadastrado
        E que o organizador adiciona o papel de ORIENTADOR para o convidado
        Quando o organizador tenta convidar uma pessoa com nome FULANO o email invalido inválido@@gmail.com
        Então uma mensagem de erro de impedimento é retornada
        
     Cenário: O organizador convida pessoas para participarem de um evento inativo
        Dado que existe um evento inativo
        Então a mensagem de impedimento é retornada para o organizador 
	
	  	 