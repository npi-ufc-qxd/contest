# language: pt
Funcionalidade: O organizador decide visualizar os detalhes de um evento
	
	Eu como organizador gostaria de visualizar os detalhes de um evento
	O professor deve ser capaz de visualizar os detalhes de eventos ativos
	Apenas eventos ativos devem aparecer.
	O professor deve escolher o evento desejado
			
		
	  Contexto:
        Dado Estou logado no sistema como organizador
        
	  Cenário: Organizador visualiza os detalhes de um evento ativo com sucesso 
		Quando Escolho visualizar os detalhes de um evento ativo com id 1
		Então Deve ser mostrado os periodos de submissão e revisão de trabalhos do evento
		E Deve ser mostrado a quantidade de Trilhas no evento
		E Deve ser mostrado a quantidade de Revisores no evento
		E Deve ser mostrado a quantidade de Trabalhos submetidos, revisados e não revisados no evento
			
			
	  Cenário: Organizador visualiza os detalhes de um evento que está inativo 
		Quando Escolho visualizar os detalhes de um evento inativo com id 2
		Então Deve ser mostrado uma mensagem informando que o evento está inátivo
		
	  Cenário: Organizador visualiza os detalhes de um evento inexistente 
		Quando Escolho visualizar os detalhes de um evento inexistente com id 2
		Então Deve ser mostrado uma mensagem informando que o evento não existe