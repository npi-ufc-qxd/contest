# language: pt
	Funcionalidade: Organizador cadastra trilhas de submissão
		
		Eu como organizador gostaria de cadastrar trilhas de submissão.
		O organizador deve informar obrigatoriamente o nome da trilha.
	
		
	      	Contexto:
	        	Dado o administrador deseja cadastrar um evento.
		
			Cenário: O organizador cadastra uma trilha de submissão
			    Quando existe um organizador OrganizadorNome
			    E que existe um evento EventoNome e DescricaoEvento
			    Quando o organizador cadastra uma trilha de submissão
			    Então a trilha de submissão é cadastrada
