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
		E Realizo a revisão de um artigo com todos os critérios obrigatórios selecionados
		E O Trabalho a ser revisado não existe
		Então A revisão não é registrada
		E Uma mensagem de erro deve ser mostrada