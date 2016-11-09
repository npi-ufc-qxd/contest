# language: pt
Funcionalidade: Professor revisa trabalhos submetidos
	
	Eu como professor gostaria de avaliar os artigos que preciso revisar
	Professor pode fazer downloads do artigo que ele deve revisar
	O professor deve preencher todos os dados do formulário de revisão
	O professor pode alterar a revisão enquanto o prazo de revisão estiver aberto
	Professor é chefe de sessão
	Professor é que será chefe de sessão
		
	  Contexto:
        Dado Estou logado no sistema como professor
        
	  Cenário: Professor revisa um trabalho com sucesso
		E Realizo a revisão de um artigo com todos os critérios obrigatórios selecionados
		Então A revisão é registrada
		E Uma mensagem de sucesso deve ser mostrada
			
	  Cenário: Professor não preenche todos os campos obrigatórios
	  	E Realizo a revisão de um artigo com um critério obrigatório não preenchido
	  	Então A revisão não é aceita
	  	E Uma mensagem de erro deve ser mostrada
	  
	  Cenário: Professor tenta revisar um trabalho inexistente
		E Preencho todos os critérios obrigatórios
		E O Trabalho a ser revisado não existe
		Então A revisão não é registrada
		E Um erro no sistema deve ser mostrado
		
	  Cenário: Professor tenta revisar um trabalho fora do prazo de revisão
	  	E Tento revisar um trabalho existente fora do prazo de revisão
	  	Então uma mensagem informativa deve ser mostrada
	  	
	  Cenário: Professor tenta revisar um trabalho em que não é revisor
	  	E Tento revisar um trabalho que não sou revisor
	  	Então uma mensagem de erro de permissão deve ser mostrada
	  	
	  Cenário: Professor tenta baixar um trabalho em que não é revisor e nem organizador do evento do trabalho
	  	E Tento baixar um trabalho em que não sou revisor e nem organizador do evento do trabalho
		Então um erro deve ser mostrado