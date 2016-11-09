# language: pt
	Funcionalidade: Gerar Certificado dos Encontros Universitarios.

		Eu como organizador do evento gostaria de gerar os certificados em pdfs
		para os encontros universitarios
		
		Cenário: O organizador gera pdfs para organizadores
			Dado que o organizador deseja gerar o pdf dos organizadores
			Quando ele seleciona os organizadores e manda gerar o pdf
	   	 	Então o pdf dos organizadores e gerado
	    
	    Cenário: O organizador gera pdfs para revisores
			Dado que o organizador deseja gerar o pdf dos revisores
			Quando ele seleciona os revisores e manda gerar o pdf
	   	 	Então o pdf dos revisores e gerado
	    
	    Cenário: O organizador gera pdfs para os trabalhos
			Dado que o organizador deseja gerar o pdf dos trabalhos
			Quando ele seleciona os trabalhos e manda gerar o pdf
	   	 	Então o pdf dos trabalhos e gerado