# language: pt
Funcionalidade: Altera Detalhes do Evento
	
	Contexto:
		Dado que existe um evento com submissões e revisões realizadas
	
	Cenário: O organizador altera os detalhes de data final final do prazo de revisão do prazo 
	         de um evento com submissões e revisões realizadas
        Quando o organizador altera a data final do prazo de revisão para uma data válida
        Então deve ser redirecionado para a página de eventos ativos
        
   Cenário: O organizador altera os detalhes do prazo de submissão de um evento com submissões 
             e revisões realizadas
        Quando o organizador altera o prazo de submissão
        Então uma mensagem de erro é retornada
    
    Cenário: O organizador altera os detalhes da data final do prazo de submissão de um evento 
             com apenas submissões realizadas
        Dado que existe um evento com apenas submissões realizadas
        Quando o organizador altera a data final do prazo de submissão para uma data válida
        Então a data final do prazo de submissão é alterada com sucesso
   	
   	Cenário: O organizador altera os detalhes do prazo de revisão de um evento sem submissões 
   			 e revisões realizadas
        Dado que existe um evento sem submissões e revisões alteradas
        Quando o organizador altera os detalhes do prazo de revisão
        Então uma mensagem de erro na data de revisão é mostrada
        
