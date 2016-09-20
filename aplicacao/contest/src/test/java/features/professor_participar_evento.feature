# language: pt
Funcionalidade: O professor decide participar de um evento
	
	Eu como professor gostaria de participar de um evento
	O professor deve ser capaz de buscar por eventos ativos
	Apenas eventos públicos devem aparecer.
	O professor deve escolher o evento e confirmar sua inscrição
	Automaticamente será atribuído ao professor o papel de revisor
		
		
	  Contexto:
        Dado Estou logado no sistema como professor
        
	  Cenário: Professor participa de um evento com sucesso 
		E Realizo uma busca por eventos ativos no sistema
		Quando Escolho participar de um evento ativo com id 2
		Então Confirmo minha incrição
		E Deve ser mostrado uma mensagem de feedback
			
	  Cenário: Verificar os eventos listados para o professor
	  	Quando Realizo uma busca por eventos ativos
	  	Então Deve ser mostrado apenas eventos públicos
	  
	  Cenário: Buscar por evento 
		Quando Realizo uma busca por um evento
		Então Deve ser listado os eventos ativos e públicos