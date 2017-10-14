# language: pt
Funcionalidade: Enviar Trabalho
	Eu como autor de um trabalho gostaria de enviar meu trabalho para um evento que participo
	
	Contexto:
		Dado que o autor seleciona um evento que ele participa e o prazo esta vigente
			
	Cenário: Adicionar Trabalho com sucesso
		Quando ele preenche os campos corretamente e escolhe um arquivo .pdf
		Então o autor deve ser redirecionado para a página meusTrabalhos com uma mensagem de sucesso
	
	Cenário: Deixar campo titulo em branco
	    Quando ele não preenche o campo titulo
	    Então deve ser mostrado uma mensagem de erro dizendo que o titulo está em branco
	
	Cenário: Deixa campo nome do orientador e email em branco
	    Quando ele não preenche os campos nom do orientador e email do orientador
	    Então deve ser mostrado uma mensagem de erro que há campos em branco
	
	Cenário: Enviar arquivo no formato invalido
	    Quando o autor escolhe um arquivo em um formato diferete de .pdf
	    Então deve ser mostrado uma mensagem de erro que o formato do arquivo é invalido
	
	Cenário: Cadastrar com evento inexistente
	    Quando ele muda o id do evento para um inexistente
	    Então deve ser mostrado uma mensagem de erro de evento não existe